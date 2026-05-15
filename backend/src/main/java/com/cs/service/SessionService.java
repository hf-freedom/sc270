package com.cs.service;

import com.cs.enums.AgentStatus;
import com.cs.enums.ProblemType;
import com.cs.enums.SessionStatus;
import com.cs.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SessionService {

    @Autowired
    private DataStorageService dataStorageService;

    @Value("${agent.max-capacity:3}")
    private int maxCapacity;

    @Value("${agent.timeout-seconds:60}")
    private int timeoutSeconds;

    @Value("${vip.max-jump-count:2}")
    private int maxVipJumpCount;

    public Session createSession(User user, ProblemType problemType, String problemDesc) {
        Session session = new Session();
        session.setSessionId(UUID.randomUUID().toString());
        session.setUser(user);
        session.setProblemType(problemType);
        session.setProblemDesc(problemDesc);
        session.setCreateTime(System.currentTimeMillis());
        session.setLastResponseTime(System.currentTimeMillis());
        session.setVip(user.isVip());
        session.setTimeoutCount(0);

        int priority = calculatePriority(session);
        session.setPriority(priority);

        if (problemType == ProblemType.COMPLAINT) {
            session.setNeedQualityCheck(true);
            session.setPriority(priority + 50);
        }

        mergeHistorySessions(session);

        String skillGroupId = findSkillGroup(problemType);
        session.setSkillGroupId(skillGroupId);

        session.setStatus(SessionStatus.QUEUING);
        dataStorageService.getSessionMap().put(session.getSessionId(), session);

        synchronized (dataStorageService.getSessionQueue()) {
            dataStorageService.getSessionQueue().offer(session);
        }

        String userId = user.getUserId();
        dataStorageService.getUserHistoryMap().computeIfAbsent(userId, k -> new ArrayList<>()).add(session);

        tryAssignSession();

        return session;
    }

    private int calculatePriority(Session session) {
        int priority = 0;
        if (session.isVip()) {
            priority += 30;
        }
        if (session.getProblemType() == ProblemType.COMPLAINT) {
            priority += 50;
        }
        return priority;
    }

    private void mergeHistorySessions(Session newSession) {
        String userId = newSession.getUser().getUserId();
        List<Session> userHistory = dataStorageService.getUserHistoryMap().get(userId);
        
        if (userHistory != null) {
            for (Session historySession : userHistory) {
                if (historySession.getStatus() == SessionStatus.QUEUING ||
                    historySession.getStatus() == SessionStatus.ASSIGNED ||
                    historySession.getStatus() == SessionStatus.PROCESSING) {
                    
                    newSession.getMergedSessionIds().add(historySession.getSessionId());
                    
                    String agentId = historySession.getAssignedAgentId();
                    if (agentId != null) {
                        Agent agent = dataStorageService.getAgentMap().get(agentId);
                        if (agent != null) {
                            agent.getActiveSessionIds().remove(historySession.getSessionId());
                            agent.setCurrentLoad(agent.getCurrentLoad() - 1);
                            if (agent.getCurrentLoad() < agent.getMaxCapacity()) {
                                agent.setStatus(SessionStatus.QUEUING != null ? AgentStatus.ONLINE : agent.getStatus());
                            }
                        }
                    }
                    
                    historySession.setStatus(SessionStatus.CLOSED);
                    historySession.setAssignedAgentId(null);
                }
            }
            
            synchronized (dataStorageService.getSessionQueue()) {
                List<Session> toRemove = new ArrayList<>();
                for (Session queued : dataStorageService.getSessionQueue()) {
                    if (newSession.getMergedSessionIds().contains(queued.getSessionId())) {
                        toRemove.add(queued);
                    }
                }
                for (Session remove : toRemove) {
                    dataStorageService.getSessionQueue().remove(remove);
                }
            }
        }
    }

    private String findSkillGroup(ProblemType problemType) {
        for (SkillGroup group : dataStorageService.getSkillGroupMap().values()) {
            if (group.getSupportedTypes().contains(problemType)) {
                return group.getGroupId();
            }
        }
        return "G001";
    }

    public void tryAssignSession() {
        PriorityQueue<Session> queue = dataStorageService.getSessionQueue();
        
        List<Session> tempList = new ArrayList<>();
        synchronized (queue) {
            while (!queue.isEmpty()) {
                Session session = queue.poll();
                if (session.getStatus() == SessionStatus.QUEUING) {
                    tempList.add(session);
                }
            }
            for (Session s : tempList) {
                queue.offer(s);
            }
        }

        for (Session session : tempList) {
            if (tryAssignToAgent(session)) {
                synchronized (queue) {
                    queue.remove(session);
                }
            }
        }
    }

    private boolean tryAssignToAgent(Session session) {
        String skillGroupId = session.getSkillGroupId();
        SkillGroup skillGroup = dataStorageService.getSkillGroupMap().get(skillGroupId);
        
        if (skillGroup == null) {
            return false;
        }

        List<Agent> availableAgents = new ArrayList<>();
        for (String agentId : skillGroup.getAgentIds()) {
            Agent agent = dataStorageService.getAgentMap().get(agentId);
            if (agent != null && agent.getStatus() == AgentStatus.ONLINE &&
                agent.getCurrentLoad() < agent.getMaxCapacity()) {
                availableAgents.add(agent);
            }
        }

        if (availableAgents.isEmpty()) {
            for (String agentId : dataStorageService.getAgentMap().keySet()) {
                Agent agent = dataStorageService.getAgentMap().get(agentId);
                if (agent != null && agent.getStatus() == AgentStatus.ONLINE &&
                    agent.getCurrentLoad() < agent.getMaxCapacity() &&
                    !skillGroup.getAgentIds().contains(agentId)) {
                    availableAgents.add(agent);
                }
            }
        }

        if (!availableAgents.isEmpty()) {
            availableAgents.sort(Comparator.comparingInt(Agent::getCurrentLoad));
            Agent targetAgent = availableAgents.get(0);
            assignSessionToAgent(session, targetAgent);
            return true;
        }

        return false;
    }

    private void assignSessionToAgent(Session session, Agent agent) {
        session.setAssignedAgentId(agent.getAgentId());
        session.setAssignTime(System.currentTimeMillis());
        session.setLastResponseTime(System.currentTimeMillis());
        session.setStatus(SessionStatus.ASSIGNED);

        agent.setCurrentLoad(agent.getCurrentLoad() + 1);
        agent.getActiveSessionIds().add(session.getSessionId());
        
        if (agent.getCurrentLoad() >= agent.getMaxCapacity()) {
            agent.setStatus(AgentStatus.BUSY);
        }
    }

    public void agentResponse(String sessionId) {
        Session session = dataStorageService.getSessionMap().get(sessionId);
        if (session != null) {
            session.setLastResponseTime(System.currentTimeMillis());
            session.setStatus(SessionStatus.PROCESSING);
        }
    }

    public void closeSession(String sessionId) {
        Session session = dataStorageService.getSessionMap().get(sessionId);
        if (session != null) {
            session.setStatus(SessionStatus.CLOSED);
            
            String agentId = session.getAssignedAgentId();
            if (agentId != null) {
                Agent agent = dataStorageService.getAgentMap().get(agentId);
                if (agent != null) {
                    agent.setCurrentLoad(agent.getCurrentLoad() - 1);
                    agent.getActiveSessionIds().remove(sessionId);
                    if (agent.getCurrentLoad() < agent.getMaxCapacity() && 
                        agent.getStatus() == AgentStatus.BUSY) {
                        agent.setStatus(AgentStatus.ONLINE);
                    }
                }
            }

            if (session.isNeedQualityCheck()) {
                session.setStatus(SessionStatus.QUALITY_CHECK);
                dataStorageService.getQualityCheckList().add(session);
            }

            tryAssignSession();
        }
    }

    public void checkAndTransferTimeoutSessions() {
        long now = System.currentTimeMillis();
        long timeoutMs = timeoutSeconds * 1000L;

        List<Session> needTransfer = new ArrayList<>();
        for (Session session : dataStorageService.getSessionMap().values()) {
            if ((session.getStatus() == SessionStatus.ASSIGNED || 
                 session.getStatus() == SessionStatus.PROCESSING) &&
                now - session.getLastResponseTime() > timeoutMs) {
                needTransfer.add(session);
            }
        }

        for (Session session : needTransfer) {
            transferSession(session);
        }
    }

    private void transferSession(Session session) {
        String oldAgentId = session.getAssignedAgentId();
        if (oldAgentId != null) {
            Agent oldAgent = dataStorageService.getAgentMap().get(oldAgentId);
            if (oldAgent != null) {
                oldAgent.setCurrentLoad(oldAgent.getCurrentLoad() - 1);
                oldAgent.getActiveSessionIds().remove(session.getSessionId());
                if (oldAgent.getCurrentLoad() < oldAgent.getMaxCapacity() &&
                    oldAgent.getStatus() == AgentStatus.BUSY) {
                    oldAgent.setStatus(AgentStatus.ONLINE);
                }
            }
        }

        session.setAssignedAgentId(null);
        session.setAssignTime(0);
        session.setStatus(SessionStatus.TRANSFERRED);
        session.setTimeoutCount(session.getTimeoutCount() + 1);
        session.setStatus(SessionStatus.QUEUING);

        synchronized (dataStorageService.getSessionQueue()) {
            dataStorageService.getSessionQueue().offer(session);
        }

        tryAssignSession();
    }

    public void recycleOfflineAgentSessions() {
        long now = System.currentTimeMillis();
        long offlineThreshold = 3600000;

        for (Agent agent : dataStorageService.getAgentMap().values()) {
            if (agent.getStatus() == AgentStatus.OFFLINE) {
                List<String> sessionIds = new ArrayList<>(agent.getActiveSessionIds());
                for (String sessionId : sessionIds) {
                    Session session = dataStorageService.getSessionMap().get(sessionId);
                    if (session != null) {
                        agent.getActiveSessionIds().remove(sessionId);
                        agent.setCurrentLoad(agent.getCurrentLoad() - 1);
                        
                        session.setAssignedAgentId(null);
                        session.setStatus(SessionStatus.QUEUING);
                        
                        synchronized (dataStorageService.getSessionQueue()) {
                            dataStorageService.getSessionQueue().offer(session);
                        }
                    }
                }
            }
        }

        tryAssignSession();
    }

    public void setAgentStatus(String agentId, AgentStatus status) {
        Agent agent = dataStorageService.getAgentMap().get(agentId);
        if (agent != null) {
            agent.setStatus(status);
            agent.setLastHeartbeat(System.currentTimeMillis());
            if (status == AgentStatus.ONLINE) {
                tryAssignSession();
            }
        }
    }

    public void updateAgentHeartbeat(String agentId) {
        Agent agent = dataStorageService.getAgentMap().get(agentId);
        if (agent != null) {
            agent.setLastHeartbeat(System.currentTimeMillis());
            if (agent.getStatus() == AgentStatus.OFFLINE) {
                agent.setStatus(AgentStatus.ONLINE);
            }
        }
    }

    public SystemStats getSystemStats() {
        SystemStats stats = new SystemStats();
        stats.setTimestamp(System.currentTimeMillis());
        
        Map<String, Agent> agentMap = dataStorageService.getAgentMap();
        stats.setTotalAgents(agentMap.size());
        
        int online = 0, busy = 0, offline = 0;
        int totalLoad = 0;
        Map<String, Integer> agentLoadMap = new HashMap<>();
        
        for (Agent agent : agentMap.values()) {
            agentLoadMap.put(agent.getAgentName(), agent.getCurrentLoad());
            totalLoad += agent.getCurrentLoad();
            
            switch (agent.getStatus()) {
                case ONLINE: online++; break;
                case BUSY: busy++; break;
                case OFFLINE: offline++; break;
                case AWAY: break;
            }
        }
        
        stats.setOnlineAgents(online);
        stats.setBusyAgents(busy);
        stats.setOfflineAgents(offline);
        stats.setAverageLoad(agentMap.size() > 0 ? (double) totalLoad / agentMap.size() : 0);
        stats.setAgentLoadMap(agentLoadMap);

        int queueSize = 0, vipQueueSize = 0;
        synchronized (dataStorageService.getSessionQueue()) {
            queueSize = dataStorageService.getSessionQueue().size();
            for (Session s : dataStorageService.getSessionQueue()) {
                if (s.isVip()) vipQueueSize++;
            }
        }
        stats.setQueueLength(queueSize);
        stats.setVipQueueLength(vipQueueSize);

        int totalSessions = 0, processing = 0, timeout = 0;
        for (Session session : dataStorageService.getSessionMap().values()) {
            totalSessions++;
            if (session.getStatus() == SessionStatus.PROCESSING || 
                session.getStatus() == SessionStatus.ASSIGNED) {
                processing++;
            }
            timeout += session.getTimeoutCount();
        }
        stats.setTotalSessions(totalSessions);
        stats.setProcessingSessions(processing);
        stats.setTimeoutCount(timeout);

        return stats;
    }

    public List<Agent> getAllAgents() {
        return new ArrayList<>(dataStorageService.getAgentMap().values());
    }

    public List<Session> getAllSessions() {
        return new ArrayList<>(dataStorageService.getSessionMap().values());
    }

    public List<SkillGroup> getAllSkillGroups() {
        return new ArrayList<>(dataStorageService.getSkillGroupMap().values());
    }

    public List<Session> getQueueSessions() {
        List<Session> sessions = new ArrayList<>();
        synchronized (dataStorageService.getSessionQueue()) {
            PriorityQueue<Session> tempQueue = new PriorityQueue<>(dataStorageService.getSessionQueue());
            while (!tempQueue.isEmpty()) {
                sessions.add(tempQueue.poll());
            }
        }
        return sessions;
    }

    public List<Session> getQualityCheckSessions() {
        return new ArrayList<>(dataStorageService.getQualityCheckList());
    }
}

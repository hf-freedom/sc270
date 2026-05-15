package com.cs.service;

import com.cs.enums.AgentStatus;
import com.cs.enums.ProblemType;
import com.cs.model.Agent;
import com.cs.model.Session;
import com.cs.model.SkillGroup;
import com.cs.model.User;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DataStorageService {
    
    private final Map<String, Agent> agentMap = new ConcurrentHashMap<>();
    private final Map<String, SkillGroup> skillGroupMap = new ConcurrentHashMap<>();
    private final Map<String, Session> sessionMap = new ConcurrentHashMap<>();
    private final Map<String, List<Session>> userHistoryMap = new ConcurrentHashMap<>();
    private final PriorityQueue<Session> sessionQueue = new PriorityQueue<>(
            (s1, s2) -> Integer.compare(s2.getPriority(), s1.getPriority())
    );
    private final List<Session> qualityCheckList = new ArrayList<>();

    @PostConstruct
    public void init() {
        initSkillGroups();
        initAgents();
    }

    private void initSkillGroups() {
        SkillGroup group1 = new SkillGroup();
        group1.setGroupId("G001");
        group1.setGroupName("咨询服务组");
        group1.setSupportedTypes(Arrays.asList(ProblemType.CONSULT, ProblemType.OTHER));
        skillGroupMap.put(group1.getGroupId(), group1);

        SkillGroup group2 = new SkillGroup();
        group2.setGroupId("G002");
        group2.setGroupName("投诉处理组");
        group2.setSupportedTypes(Arrays.asList(ProblemType.COMPLAINT));
        skillGroupMap.put(group2.getGroupId(), group2);

        SkillGroup group3 = new SkillGroup();
        group3.setGroupId("G003");
        group3.setGroupName("技术支持组");
        group3.setSupportedTypes(Arrays.asList(ProblemType.TECHNICAL));
        skillGroupMap.put(group3.getGroupId(), group3);

        SkillGroup group4 = new SkillGroup();
        group4.setGroupId("G004");
        group4.setGroupName("账务处理组");
        group4.setSupportedTypes(Arrays.asList(ProblemType.BILLING, ProblemType.REFUND));
        skillGroupMap.put(group4.getGroupId(), group4);
    }

    private void initAgents() {
        Agent agent1 = new Agent();
        agent1.setAgentId("A001");
        agent1.setAgentName("张三");
        agent1.setStatus(AgentStatus.ONLINE);
        agent1.setMaxCapacity(3);
        agent1.setCurrentLoad(0);
        agent1.setSkillGroupIds(Arrays.asList("G001", "G002"));
        agent1.setLastHeartbeat(System.currentTimeMillis());
        agentMap.put(agent1.getAgentId(), agent1);
        skillGroupMap.get("G001").getAgentIds().add(agent1.getAgentId());
        skillGroupMap.get("G002").getAgentIds().add(agent1.getAgentId());

        Agent agent2 = new Agent();
        agent2.setAgentId("A002");
        agent2.setAgentName("李四");
        agent2.setStatus(AgentStatus.ONLINE);
        agent2.setMaxCapacity(3);
        agent2.setCurrentLoad(0);
        agent2.setSkillGroupIds(Arrays.asList("G003"));
        agent2.setLastHeartbeat(System.currentTimeMillis());
        agentMap.put(agent2.getAgentId(), agent2);
        skillGroupMap.get("G003").getAgentIds().add(agent2.getAgentId());

        Agent agent3 = new Agent();
        agent3.setAgentId("A003");
        agent3.setAgentName("王五");
        agent3.setStatus(AgentStatus.ONLINE);
        agent3.setMaxCapacity(3);
        agent3.setCurrentLoad(0);
        agent3.setSkillGroupIds(Arrays.asList("G004"));
        agent3.setLastHeartbeat(System.currentTimeMillis());
        agentMap.put(agent3.getAgentId(), agent3);
        skillGroupMap.get("G004").getAgentIds().add(agent3.getAgentId());

        Agent agent4 = new Agent();
        agent4.setAgentId("A004");
        agent4.setAgentName("赵六");
        agent4.setStatus(AgentStatus.OFFLINE);
        agent4.setMaxCapacity(3);
        agent4.setCurrentLoad(0);
        agent4.setSkillGroupIds(Arrays.asList("G001", "G003"));
        agent4.setLastHeartbeat(System.currentTimeMillis());
        agentMap.put(agent4.getAgentId(), agent4);
    }

    public Map<String, Agent> getAgentMap() {
        return agentMap;
    }

    public Map<String, SkillGroup> getSkillGroupMap() {
        return skillGroupMap;
    }

    public Map<String, Session> getSessionMap() {
        return sessionMap;
    }

    public Map<String, List<Session>> getUserHistoryMap() {
        return userHistoryMap;
    }

    public PriorityQueue<Session> getSessionQueue() {
        return sessionQueue;
    }

    public List<Session> getQualityCheckList() {
        return qualityCheckList;
    }
}

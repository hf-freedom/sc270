package com.cs.model;

import com.cs.enums.AgentStatus;

import java.util.ArrayList;
import java.util.List;

public class Agent {
    private String agentId;
    private String agentName;
    private AgentStatus status;
    private int maxCapacity;
    private int currentLoad;
    private List<String> skillGroupIds = new ArrayList<>();
    private List<String> activeSessionIds = new ArrayList<>();
    private long lastHeartbeat;

    public String getAgentId() { return agentId; }
    public void setAgentId(String agentId) { this.agentId = agentId; }
    public String getAgentName() { return agentName; }
    public void setAgentName(String agentName) { this.agentName = agentName; }
    public AgentStatus getStatus() { return status; }
    public void setStatus(AgentStatus status) { this.status = status; }
    public int getMaxCapacity() { return maxCapacity; }
    public void setMaxCapacity(int maxCapacity) { this.maxCapacity = maxCapacity; }
    public int getCurrentLoad() { return currentLoad; }
    public void setCurrentLoad(int currentLoad) { this.currentLoad = currentLoad; }
    public List<String> getSkillGroupIds() { return skillGroupIds; }
    public void setSkillGroupIds(List<String> skillGroupIds) { this.skillGroupIds = skillGroupIds; }
    public List<String> getActiveSessionIds() { return activeSessionIds; }
    public void setActiveSessionIds(List<String> activeSessionIds) { this.activeSessionIds = activeSessionIds; }
    public long getLastHeartbeat() { return lastHeartbeat; }
    public void setLastHeartbeat(long lastHeartbeat) { this.lastHeartbeat = lastHeartbeat; }
}

package com.cs.model;

import java.util.Map;

public class SystemStats {
    private long timestamp;
    private int totalAgents;
    private int onlineAgents;
    private int busyAgents;
    private int offlineAgents;
    private double averageLoad;
    private int queueLength;
    private int vipQueueLength;
    private int timeoutCount;
    private int totalSessions;
    private int processingSessions;
    private Map<String, Integer> agentLoadMap;

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public int getTotalAgents() { return totalAgents; }
    public void setTotalAgents(int totalAgents) { this.totalAgents = totalAgents; }
    public int getOnlineAgents() { return onlineAgents; }
    public void setOnlineAgents(int onlineAgents) { this.onlineAgents = onlineAgents; }
    public int getBusyAgents() { return busyAgents; }
    public void setBusyAgents(int busyAgents) { this.busyAgents = busyAgents; }
    public int getOfflineAgents() { return offlineAgents; }
    public void setOfflineAgents(int offlineAgents) { this.offlineAgents = offlineAgents; }
    public double getAverageLoad() { return averageLoad; }
    public void setAverageLoad(double averageLoad) { this.averageLoad = averageLoad; }
    public int getQueueLength() { return queueLength; }
    public void setQueueLength(int queueLength) { this.queueLength = queueLength; }
    public int getVipQueueLength() { return vipQueueLength; }
    public void setVipQueueLength(int vipQueueLength) { this.vipQueueLength = vipQueueLength; }
    public int getTimeoutCount() { return timeoutCount; }
    public void setTimeoutCount(int timeoutCount) { this.timeoutCount = timeoutCount; }
    public int getTotalSessions() { return totalSessions; }
    public void setTotalSessions(int totalSessions) { this.totalSessions = totalSessions; }
    public int getProcessingSessions() { return processingSessions; }
    public void setProcessingSessions(int processingSessions) { this.processingSessions = processingSessions; }
    public Map<String, Integer> getAgentLoadMap() { return agentLoadMap; }
    public void setAgentLoadMap(Map<String, Integer> agentLoadMap) { this.agentLoadMap = agentLoadMap; }
}

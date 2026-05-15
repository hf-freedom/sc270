package com.cs.model;

import com.cs.enums.SessionStatus;
import com.cs.enums.ProblemType;

import java.util.ArrayList;
import java.util.List;

public class Session {
    private String sessionId;
    private User user;
    private ProblemType problemType;
    private String problemDesc;
    private SessionStatus status;
    private String assignedAgentId;
    private String skillGroupId;
    private int priority;
    private boolean isVip;
    private long createTime;
    private long assignTime;
    private long lastResponseTime;
    private List<String> mergedSessionIds = new ArrayList<>();
    private boolean needQualityCheck;
    private int timeoutCount;

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public ProblemType getProblemType() { return problemType; }
    public void setProblemType(ProblemType problemType) { this.problemType = problemType; }
    public String getProblemDesc() { return problemDesc; }
    public void setProblemDesc(String problemDesc) { this.problemDesc = problemDesc; }
    public SessionStatus getStatus() { return status; }
    public void setStatus(SessionStatus status) { this.status = status; }
    public String getAssignedAgentId() { return assignedAgentId; }
    public void setAssignedAgentId(String assignedAgentId) { this.assignedAgentId = assignedAgentId; }
    public String getSkillGroupId() { return skillGroupId; }
    public void setSkillGroupId(String skillGroupId) { this.skillGroupId = skillGroupId; }
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    public boolean isVip() { return isVip; }
    public void setVip(boolean vip) { isVip = vip; }
    public long getCreateTime() { return createTime; }
    public void setCreateTime(long createTime) { this.createTime = createTime; }
    public long getAssignTime() { return assignTime; }
    public void setAssignTime(long assignTime) { this.assignTime = assignTime; }
    public long getLastResponseTime() { return lastResponseTime; }
    public void setLastResponseTime(long lastResponseTime) { this.lastResponseTime = lastResponseTime; }
    public List<String> getMergedSessionIds() { return mergedSessionIds; }
    public void setMergedSessionIds(List<String> mergedSessionIds) { this.mergedSessionIds = mergedSessionIds; }
    public boolean isNeedQualityCheck() { return needQualityCheck; }
    public void setNeedQualityCheck(boolean needQualityCheck) { this.needQualityCheck = needQualityCheck; }
    public int getTimeoutCount() { return timeoutCount; }
    public void setTimeoutCount(int timeoutCount) { this.timeoutCount = timeoutCount; }
}

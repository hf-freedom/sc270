package com.cs.model;

import com.cs.enums.ProblemType;

import java.util.ArrayList;
import java.util.List;

public class SkillGroup {
    private String groupId;
    private String groupName;
    private List<ProblemType> supportedTypes = new ArrayList<>();
    private List<String> agentIds = new ArrayList<>();

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
    public List<ProblemType> getSupportedTypes() { return supportedTypes; }
    public void setSupportedTypes(List<ProblemType> supportedTypes) { this.supportedTypes = supportedTypes; }
    public List<String> getAgentIds() { return agentIds; }
    public void setAgentIds(List<String> agentIds) { this.agentIds = agentIds; }
}

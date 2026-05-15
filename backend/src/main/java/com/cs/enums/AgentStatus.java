package com.cs.enums;

public enum AgentStatus {
    ONLINE("在线"),
    BUSY("忙碌"),
    OFFLINE("离线"),
    AWAY("离开");

    private String desc;

    AgentStatus(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}

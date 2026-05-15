package com.cs.enums;

public enum SessionStatus {
    QUEUING("排队中"),
    ASSIGNED("已分配"),
    PROCESSING("处理中"),
    TRANSFERRED("已转派"),
    CLOSED("已关闭"),
    QUALITY_CHECK("质检中");

    private String desc;

    SessionStatus(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}

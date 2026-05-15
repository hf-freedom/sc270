package com.cs.enums;

public enum ProblemType {
    CONSULT("咨询"),
    COMPLAINT("投诉"),
    TECHNICAL("技术支持"),
    BILLING("账单问题"),
    REFUND("退款"),
    OTHER("其他");

    private String desc;

    ProblemType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}

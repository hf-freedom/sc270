package com.cs.dto;

import com.cs.enums.ProblemType;

public class CreateSessionRequest {
    private String userId;
    private String userName;
    private boolean isVip;
    private String phone;
    private String email;
    private ProblemType problemType;
    private String problemDesc;

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public boolean isVip() { return isVip; }
    public void setVip(boolean vip) { isVip = vip; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public ProblemType getProblemType() { return problemType; }
    public void setProblemType(ProblemType problemType) { this.problemType = problemType; }
    public String getProblemDesc() { return problemDesc; }
    public void setProblemDesc(String problemDesc) { this.problemDesc = problemDesc; }
}

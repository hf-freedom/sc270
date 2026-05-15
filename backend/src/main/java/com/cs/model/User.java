package com.cs.model;

public class User {
    private String userId;
    private String userName;
    private boolean isVip;
    private String phone;
    private String email;

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
}

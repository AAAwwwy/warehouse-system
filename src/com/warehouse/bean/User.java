package com.warehouse.bean;

public class User {
    private Integer id;         // 用户ID
    private String username;    // 用户名
    private String password;    // 密码
    private String role;        // 角色

    // 无参构造
    public User() {}

    // 有参构造（常用字段）
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // getter和setter方法
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
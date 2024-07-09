package com.example.daiquiri;

public class UsernameInfo extends TransInfo {
    private static final long serialVersionUID = 1L;
    private String username;

    public UsernameInfo(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
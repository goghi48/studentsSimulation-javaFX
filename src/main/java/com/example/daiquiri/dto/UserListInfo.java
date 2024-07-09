package com.example.daiquiri;

public class UserListInfo extends TransInfo {
    private static final long serialVersionUID = 1L;
    private String[] usernames;

    public UserListInfo(String[] usernames) {
        this.usernames = usernames;
    }

    public String[] getUsernames() {
        return usernames;
    }
}
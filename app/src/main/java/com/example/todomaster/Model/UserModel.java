package com.example.todomaster.Model;

public class UserModel {
    private String gmailUser;
    private String passUser;

    public UserModel(String gmailUser, String passUser) {
        this.gmailUser = gmailUser;
        this.passUser = passUser;
    }

    public String getGmailUser() {
        return gmailUser;
    }

    public void setGmailUser(String gmailUser) {
        this.gmailUser = gmailUser;
    }

    public String getPassUser() {
        return passUser;
    }

    public void setPassUser(String passUser) {
        this.passUser = passUser;
    }
}


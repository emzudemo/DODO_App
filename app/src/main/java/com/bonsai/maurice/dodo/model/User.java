package com.bonsai.maurice.dodo.model;

import java.io.Serializable;

/**
 * Created by maurice on 04.07.17.
 */

public class User implements Serializable {
    private String email;
    private String pwd;
    public User () {
    }
    public User (String email, String pwd){
        this.email = email;
        this.pwd = pwd;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPwd() {
        return pwd;
    }
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}

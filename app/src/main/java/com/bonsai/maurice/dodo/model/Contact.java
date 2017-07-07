package com.bonsai.maurice.dodo.model;

import java.io.Serializable;

/**
 * Created by maurice on 05.07.17.
 */

public class Contact implements Serializable {

    private long id;
    private String name;
    private String email;
    private String number;

    public Contact () {
    }

    public Contact (long id, String name, String email, String number){
        this.id = id;
        this.name = name;
        this.email = email;
        this.number = number;
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getNumber() {
        return number;
    }
    public void setNumber(String number) {
        this.number = number;
    }
}

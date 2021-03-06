package com.bonsai.maurice.dodo.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by maurice on 04.06.17.
 */

public class DataItem implements Serializable {

    private String name;

    @SerializedName("expiry")
    private long duedate;
    private long id;
    private String description;
    private boolean favourite;
    private boolean done;

    private List<Contact> contacts;

    public DataItem() {
        this.contacts = new ArrayList<Contact>();
    }

    public DataItem(String name) {
        this.name = name;
        this.contacts = new ArrayList<Contact>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDuedate() {
        return duedate;
    }

    public void setDuedate(long duedate) {
        this.duedate = duedate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public boolean getDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void addContact(Contact contact) {
        this.contacts.add(contact);
    }

    @Override
    public String toString() {
        return "DataItem{" +
                "name='" + name + '\'' +
                ", duedate=" + duedate +
                ", id=" + id +
                ", description='" + description + '\'' +
                ", favourite=" + favourite +
                ", contacts=" + contacts +
                '}';
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


}

package com.android.smartvirtualid.data.models;

public class Organization {

    private String id;
    private String name;
    private String email;
    private String password;
    private String description;

    public Organization() {
    }

    public Organization(String name, String email, String password, String description) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

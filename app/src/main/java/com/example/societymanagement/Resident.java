package com.example.societymanagement;

public class Resident {
    String name;
    String email;
    String imageurl;

    public Resident() {}

    public Resident(String name, String email, String imageurl) {
        this.name = name;
        this.email = email;
        this.imageurl = imageurl;
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

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }
}

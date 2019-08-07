package com.example.socialnetwork.Model;

public class User {

    private String image;
    private String name;
    private String business;

    private String email;
    private String search;
    private String cover;
    private String phone;
    private String uid;

    public User() {
    }

    public User(String image, String name, String business, String email, String search, String cover, String phone, String uid) {
        this.image = image;
        this.name = name;
        this.business = business;
        this.email = email;
        this.search = search;
        this.cover = cover;
        this.phone = phone;
        this.uid = uid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBusiness() {
        return business;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}

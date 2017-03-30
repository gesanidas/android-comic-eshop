package com.example.gesanidas.unipipmsplishopping;



public class User
{
    //a model class for the user data
    public String name;
    public String uid;
    public String email;
    public String firebaseToken;

    public User() {}

    public User(String name,String uid, String email)
    {
        this.name=name;
        this.uid = uid;
        this.email = email;

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }
}

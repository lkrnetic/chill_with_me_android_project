package com.example.msrprojekt001.pojo;

public class User {
    private String password;

    @Override
    public String toString() {
        return "User{" +
                "first_and_last_name='" + first_and_last_name + '\'' +
                ", ages=" + ages +
                '}';
    }

    private String first_and_last_name;
    private int ages;
    private String user_description;
    private String gender;
    private String email;
    private int id;

    public User(String first_and_last_name, String email, String password, int ages, String user_description, String gender){
        this.id = id;
        this.email = email;
        this.first_and_last_name = first_and_last_name;
        this.password = password;
        this.ages = ages;
        this.user_description = user_description;
        this.gender = gender;

    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirst_and_last_name() {
        return first_and_last_name;
    }

    public void setFirst_and_last_name(String first_and_last_name) {
        this.first_and_last_name = first_and_last_name;
    }

    public int getAges() {
        return ages;
    }

    public void setAges(int ages) {
        this.ages = ages;
    }

    public String getUser_description() {
        return user_description;
    }

    public void setUser_description(String user_description) {
        this.user_description = user_description;
    }


    public String getGender() {
        return gender;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}

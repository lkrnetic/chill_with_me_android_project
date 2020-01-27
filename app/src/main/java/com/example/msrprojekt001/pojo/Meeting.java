package com.example.msrprojekt001.pojo;

public class Meeting {
    private String event_description;
    private String address_name;
    private Double longitude;
    private Double latitude;
    private String title;
    private String event_time;
    private int user_id;
    private String user_first_and_last_name;

    public Meeting(String event_description, String address_name, Double longitude, Double latitude, String title, String event_time, int user_id, String user_first_and_last_name) {
        this.title = title;
        this.event_description = event_description;
        this.address_name = address_name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.event_time = event_time;
        this.user_id = user_id;
        this.user_first_and_last_name = user_first_and_last_name;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getEvent_description() {
        return event_description;
    }

    public String getUser_first_and_last_name() {
        return user_first_and_last_name;
    }

    public void setUser_first_and_last_name(String user_first_and_last_name) {
        this.user_first_and_last_name = user_first_and_last_name;
    }

    public void setEvent_description(String event_description) {
        this.event_description = event_description;
    }

    public String getEvent_time() {
        return event_time;
    }

    public void setEvent_time(String event_time) {
        this.event_time = event_time;
    }

    public String getAddress_name() {
        return address_name;
    }

    public void setAddress_name(String address_name) {
        this.address_name = address_name;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

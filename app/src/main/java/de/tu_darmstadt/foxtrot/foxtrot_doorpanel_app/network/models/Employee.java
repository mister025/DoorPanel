package de.tu_darmstadt.foxtrot.foxtrot_doorpanel_app.network.models;

import java.util.List;

public class Employee {
    private int id;
    private String name;
    private int age;
    private String email;
    private String room;
    private String phoneNumber;
    private String status;
    private List<Event> timeslots;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Event> getTimeslots() {
        return timeslots;
    }

    public void setTimeslots(List<Event> timeslots) {
        this.timeslots = timeslots;
    }
}

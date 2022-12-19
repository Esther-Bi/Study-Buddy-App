package com.example.studybuddy;

import com.google.firebase.database.Exclude;

import java.util.List;

public class Teacher {

    private String Id;
    private String name;
    List<String> courses;

    public Teacher() {
        //public no-arg constructor needed
    }
    public Teacher(String Id, String name , List<String> courses) {
        this.Id = Id;
        this.name = name;
        this.courses = courses;
    }
    public Teacher(String Id, String name) {
        this.name = name;
        this.Id = Id;
    }
    public Teacher(String name) {
        this.name = name;
        this.Id = "12345";
    }

    @Exclude
    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    public String getName() {
        return name;
    }

    public List<String> getCourses() {
        return courses;
    }
}

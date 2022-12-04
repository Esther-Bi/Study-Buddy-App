package com.example.studybuddy;

public class User {

    private String name;
    private int age;
    private String year;
    private String degree;
    private String gender;
    private String id;

    public User(String name, String year, String degree, String id) {
        this.name = name;
        this.age = 22;
        this.year = year;
        this.degree = degree;
        this.gender = "female";
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

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

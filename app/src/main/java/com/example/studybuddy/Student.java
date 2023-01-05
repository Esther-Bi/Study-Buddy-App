package com.example.studybuddy;

public class Student {

    private String name;
    private String age;
    private String year;
    private String degree;
    private String gender;
    private String id;
    private String phone;

    public Student(String name, String year, String degree, String gender, String age, String phone, String id) {
        this.name = name;
        this.age = age;
        this.year = year;
        this.degree = degree;
        this.gender = gender;
        this.id = id;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) { this.age = age; }

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
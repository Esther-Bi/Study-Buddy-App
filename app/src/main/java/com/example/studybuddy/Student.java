package com.example.studybuddy;

import com.google.firebase.database.Exclude;

import java.util.List;

public class Student {
    private String documentId;
    private String name;
    private String age;
    private String degree;
    private int coursenum;
    List<String> courses;

    public Student() {
        //public no-arg constructor needed
    }
    public Student(String name, String age , String degree , int coursenum , List<String> courses) {
        this.name = name;
        this.age = age;
        this.degree = degree;
        this.coursenum = coursenum;
        this.courses = courses;
    }

    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getName() {
        return name;
    }

    public String getAge() {
        return age;
    }

    public String getDegree() {
        return degree;
    }

    public int getCoursenum() {
        return coursenum;
    }

    public void setCoursenum(int coursenum) {
        this.coursenum = coursenum;
    }

    public List<String> getCourses() {
        return courses;
    }
}

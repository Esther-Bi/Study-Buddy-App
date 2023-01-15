package com.example.studybuddy.objects;

public class Course {

    private String name;
    private String grade;

    public Course() {
        //empty constructor needed
    }

    public Course(String name, String grade) {
        this.name = name;
        this.grade = grade;
    }

    public String getName() {
        return name;
    }

    public String getGrade() {
        return grade;
    }
}
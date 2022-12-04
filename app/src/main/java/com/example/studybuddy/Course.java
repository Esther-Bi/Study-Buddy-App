package com.example.studybuddy;

public class Course {

    private String name;
    private int grade;

    public Course() {
        //empty constructor needed
    }

    public Course(String name, int grade) {
        this.name = name;
        this.grade = grade;
    }

    public String getName() {
        return name;
    }

    public int getGrade() {
        return grade;
    }
}
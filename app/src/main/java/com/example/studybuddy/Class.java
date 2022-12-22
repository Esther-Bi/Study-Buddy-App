package com.example.studybuddy;

import android.text.format.DateFormat;

import java.text.SimpleDateFormat;

public class Class {

    private String studentName;
    private String teacherName;
    private String subject;
    private String date;
    private String student;
    private String teacher;

    public Class() {
        //empty constructor needed
    }

    public Class(String studentName, String teacherName, String subject, String date, String student, String teacher) {
        this.studentName = studentName;
        this.teacherName = teacherName;
        this.subject = subject;
        this.date = date;
        this.student = student;
        this.teacher = teacher;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getDate() {
//        String dateString = DateFormat.format(this.date).toString();
//        return dateString
        return this.date;
    }

//    public void setDate(String date) {
//        this.date = date;
//    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getStudent() {
        return student;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }
}

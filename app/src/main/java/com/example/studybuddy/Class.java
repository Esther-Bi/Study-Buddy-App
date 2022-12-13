package com.example.studybuddy;

import android.text.format.DateFormat;

import java.text.SimpleDateFormat;

public class Class {

    private String studentName;
    private String teacherName;
    private String subject;
    private String date;

    public Class() {
        //empty constructor needed
    }

    public Class(String studentName, String teacherName, String subject, String date) {
        this.studentName = studentName;
        this.teacherName = teacherName;
        this.subject = subject;
        this.date = date;
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

}

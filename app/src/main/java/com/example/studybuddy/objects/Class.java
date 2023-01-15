package com.example.studybuddy.objects;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Class {

    private String studentName;
    private String teacherName;
    private String subject;
    private String date;
    private String student;
    private String teacher;
    private Integer cost;
    private Integer studentApproval;
    private String past;

    public Class() {
        //empty constructor needed
    }

    public Class(String studentName, String teacherName, String subject, String date, String student, String teacher, Integer cost) {
        this.studentName = studentName;
        this.teacherName = teacherName;
        this.subject = subject;
        this.date = date;
        this.student = student;
        this.teacher = teacher;
        this.cost = cost;
        this.studentApproval = 0;
        this.past = "no";
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
        return this.date;
    }

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

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public Integer getStudentApproval() {
        return studentApproval;
    }

    public void setStudentApproval(Integer student_approval) {
        this.studentApproval = student_approval;
    }

    public String getPast() {
        return past;
    }

    public void setPast(String past) {
        this.past = past;
    }

    public Boolean compare(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy - HH:mm");
        Date this_date = null;
        Date now_date = null;

        try {
            this_date = sdf.parse(this.date);
            now_date = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (this_date.before(now_date)) {
            return true;
        }
        return false;
    }

}

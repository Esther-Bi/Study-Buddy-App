package com.example.studybuddy;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.List;

public class Teacher implements Parcelable {

    private String id;
    private String name;
    List<String> courses = new ArrayList<String>();
    List<String> dates = new ArrayList<String>();
    List<Integer> grades = new ArrayList<Integer>();
    private String age;
    private String year;
    private String degree;
    private String gender;
    private String phone;


    public Teacher() {
        //public no-arg constructor needed
    }
    public Teacher(String Id, String name , List<String> courses ) {
        this.id = Id;
        this.name = name;
        this.courses = courses;
    }
    public Teacher(String name, String year, String degree, String gender, String age,String phone, String id) {
        this.name = name;
        this.age = age;
        this.year = year;
        this.degree = degree;
        this.gender = gender;
        this.id = id;
        this.phone = phone;
    }

    public void setDocumentId(String documentId) {
        this.id = documentId;
    }

    public Teacher(String name) {
        this.name = name;
        this.id = "12345";
    }
    protected Teacher(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.courses = in.readArrayList(null);
        this.dates = in.readArrayList(null);
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String Id) {
        this.id = Id;
    }

    public String getName() {
        return name;
    }

    public List<String> getCourses() {
        return courses;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public List<String> getDates() {
        return dates;
    }

    public void setDates(List<String> dates) {
        this.dates = dates;
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

    public List<Integer> getGrades() {
        return grades;
    }

    public void setGrades(List<Integer> grades) {
        this.grades = grades;
    }

    public static final Creator<Teacher> CREATOR = new Creator<Teacher>() {
        @Override
        public Teacher createFromParcel(Parcel in) {
            return new Teacher(in);
        }

        @Override
        public Teacher[] newArray(int size) {
            return new Teacher[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeList(courses);
        dest.writeList(dates);

    }
}
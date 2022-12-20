package com.example.studybuddy;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.List;

public class Teacher implements Parcelable {

    private String Id;
    private String name;
    List<String> courses;
    List<String> dates;

    public Teacher() {
        //public no-arg constructor needed
    }
    public Teacher(String Id, String name , List<String> courses , List<String> dates) {
        this.Id = Id;
        this.name = name;
        this.courses = courses;
        this.dates = dates;
    }
    public Teacher(String Id, String name) {
        this.name = name;
        this.Id = Id;
    }
    public Teacher(String name) {
        this.name = name;
        this.Id = "12345";
    }
    protected Teacher(Parcel in) {
//        mImageResource = in.readInt();
        this.Id = in.readString();
        this.name = in.readString();
        this.courses = in.createStringArrayList();
        this.dates = in.createStringArrayList();
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

    public List<String> getDates() {
        return dates;
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
        dest.writeString(name);
        dest.writeString(Id);
        dest.writeList(courses);
        dest.writeList(dates);
    }
}

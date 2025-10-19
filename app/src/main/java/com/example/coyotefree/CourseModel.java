package com.example.coyotefree;
public class CourseModel {
    // string course_name for storing course_name
    // and imgid for storing image id.
    public String course_name;
    public String filename;
    public CourseModel(String course_name, String filename) {
        this.course_name = course_name;
        this.filename = filename;
    }
    public String getCourse_name() {
        return course_name;
    }
    public String getFile_name() {
        return filename;
    }
}

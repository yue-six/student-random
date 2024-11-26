package com.student.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Group implements Serializable {
    private String id;
    private String name;
    private String classId;
    private List<Student> students;

    public Group(String id, String name, String classId) {
        this.id = id;
        this.name = name;
        this.classId = classId;
        this.students = new ArrayList<>();  // 初始化列表
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getClassId() {
        return classId;
    }

    public List<Student> getStudents() {
        if (students == null) {  // 防止空指针
            students = new ArrayList<>();
        }
        return students;
    }

    public void addStudent(Student student) {
        if (students == null) {
            students = new ArrayList<>();
        }
        students.add(student);
        student.setGroupId(this.id);
    }

    public void removeStudent(Student student) {
        if (students != null) {
            students.remove(student);
            student.setGroupId(null);
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
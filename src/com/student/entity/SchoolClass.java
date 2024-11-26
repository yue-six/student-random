package com.student.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SchoolClass implements Serializable {
    private String id;
    private String name;
    private List<Group> groups;
    private List<Student> students;

    public SchoolClass(String id, String name) {
        this.id = id;
        this.name = name;
        this.groups = new ArrayList<>();    // 初始化列表
        this.students = new ArrayList<>();  // 初始化列表
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Group> getGroups() {
        if (groups == null) {  // 防止空指针
            groups = new ArrayList<>();
        }
        return groups;
    }

    public List<Student> getStudents() {
        if (students == null) {  // 防止空指针
            students = new ArrayList<>();
        }
        return students;
    }

    public void addGroup(Group group) {
        if (groups == null) {
            groups = new ArrayList<>();
        }
        groups.add(group);
    }

    public void removeGroup(Group group) {
        if (groups != null) {
            groups.remove(group);
            // 解散小组时清除学生的小组ID
            group.getStudents().forEach(student -> student.setGroupId(null));
        }
    }

    public void addStudent(Student student) {
        if (students == null) {
            students = new ArrayList<>();
        }
        students.add(student);
    }

    public void removeStudent(Student student) {
        if (students != null) {
            students.remove(student);
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
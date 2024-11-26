package com.student.entity;

import java.io.Serializable;

public class Student implements Serializable {
    private String id;
    private String name;
    private int score;
    private String classId;
    private String groupId;

    public Student(String id, String name) {
        this.id = id;
        this.name = name;
        this.score = 0;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public String getClassId() { return classId; }
    public void setClassId(String classId) { this.classId = classId; }
    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    @Override
    public String toString() {
        return name + " (" + score + "分)";
    }
}
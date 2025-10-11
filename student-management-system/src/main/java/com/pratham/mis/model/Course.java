package com.pratham.mis.model;

import com.pratham.mis.util.Csv.HasId;

public class Course implements HasId {
    private long id;
    private String code;
    private String title;
    private int credits;
    private Long departmentId; // nullable

    public Course() {}

    public Course(long id, String code, String title, int credits, Long departmentId) {
        this.id = id; this.code = code; this.title = title; this.credits = credits; this.departmentId = departmentId;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }
    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
}


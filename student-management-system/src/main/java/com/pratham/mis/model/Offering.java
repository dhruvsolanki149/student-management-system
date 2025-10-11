package com.pratham.mis.model;

import com.pratham.mis.util.Csv.HasId;

public class Offering implements HasId {
    private long id;
    private long courseId;
    private int semester;
    private String academicYear; // "2025-2026"
    private Long sectionId; // optional

    public Offering() {}

    public Offering(long id, long courseId, int semester, String academicYear, Long sectionId) {
        this.id = id; this.courseId = courseId; this.semester = semester; this.academicYear = academicYear; this.sectionId = sectionId;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getCourseId() { return courseId; }
    public void setCourseId(long courseId) { this.courseId = courseId; }
    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }
    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
    public Long getSectionId() { return sectionId; }
    public void setSectionId(Long sectionId) { this.sectionId = sectionId; }
}


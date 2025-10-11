package com.pratham.mis.model;

import com.pratham.mis.util.Csv.HasId;

public class Enrollment implements HasId {
    private long id;
    private long studentId;
    private long offeringId;

    public Enrollment() {}
    public Enrollment(long id, long studentId, long offeringId) {
        this.id = id; this.studentId = studentId; this.offeringId = offeringId;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getStudentId() { return studentId; }
    public void setStudentId(long studentId) { this.studentId = studentId; }
    public long getOfferingId() { return offeringId; }
    public void setOfferingId(long offeringId) { this.offeringId = offeringId; }
}


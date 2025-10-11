package com.pratham.mis.model;

import com.pratham.mis.util.Csv.HasId;

public class AttendanceEntry implements HasId {
    private long id;
    private long sessionId;
    private long studentId;
    private String status; // P/A/L
    private String remarks;

    public AttendanceEntry() {}
    public AttendanceEntry(long id, long sessionId, long studentId, String status, String remarks) {
        this.id = id; this.sessionId = sessionId; this.studentId = studentId; this.status = status; this.remarks = remarks;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getSessionId() { return sessionId; }
    public void setSessionId(long sessionId) { this.sessionId = sessionId; }
    public long getStudentId() { return studentId; }
    public void setStudentId(long studentId) { this.studentId = studentId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}

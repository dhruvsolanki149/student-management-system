package com.pratham.mis.model;

import com.pratham.mis.util.Csv.HasId;

public class AttendanceSession implements HasId {
    private long id;
    private long offeringId;
    private String date;   // yyyy-MM-dd
    private int periodNo;
    private Long takenBy;  // userId or null

    public AttendanceSession() {}
    public AttendanceSession(long id, long offeringId, String date, int periodNo, Long takenBy) {
        this.id = id; this.offeringId = offeringId; this.date = date; this.periodNo = periodNo; this.takenBy = takenBy;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getOfferingId() { return offeringId; }
    public void setOfferingId(long offeringId) { this.offeringId = offeringId; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public int getPeriodNo() { return periodNo; }
    public void setPeriodNo(int periodNo) { this.periodNo = periodNo; }
    public Long getTakenBy() { return takenBy; }
    public void setTakenBy(Long takenBy) { this.takenBy = takenBy; }
}


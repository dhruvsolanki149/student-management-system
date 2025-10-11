package com.pratham.mis.model;

import com.pratham.mis.util.Csv.HasId;

public class Student implements HasId {
    private long id;
    private String regNo;
    private String firstName;
    private String lastName;
    private String dob;     // ISO yyyy-MM-dd (string keeps CSV simple)
    private String gender;
    private String contact;
    private String program;
    private String status;  // ACTIVE/SUSPENDED/...

    public Student() {}

    public Student(long id, String regNo, String firstName, String lastName, String dob,
                   String gender, String contact, String program, String status) {
        this.id = id; this.regNo = regNo; this.firstName = firstName; this.lastName = lastName;
        this.dob = dob; this.gender = gender; this.contact = contact; this.program = program; this.status = status;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getRegNo() { return regNo; }
    public void setRegNo(String regNo) { this.regNo = regNo; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    public String getProgram() { return program; }
    public void setProgram(String program) { this.program = program; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}


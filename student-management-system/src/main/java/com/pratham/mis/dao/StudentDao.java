package com.pratham.mis.dao;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVRecord;

import com.pratham.mis.ConflictException;
import com.pratham.mis.NotFoundException;
import com.pratham.mis.model.Student;
import com.pratham.mis.util.Csv;

public class StudentDao {
    private final String path;
    private final String[] headers = {"id","regNo","firstName","lastName","dob","gender","contact","program","status"};

    public StudentDao(String path) { this.path = path; }

    private Student map(CSVRecord r) {
        return new Student(
                Long.parseLong(r.get("id")),
                r.get("regNo"),
                r.get("firstName"),
                r.get("lastName"),
                r.get("dob"),
                r.get("gender"),
                r.get("contact"),
                r.get("program"),
                r.get("status")
        );
    }

    public synchronized List<Student> all() {
        return Csv.readAll(path, this::map);
    }

    public synchronized Student byId(long id) {
        return all().stream().filter(s -> s.getId()==id).findFirst()
                .orElseThrow(() -> new NotFoundException("Student id " + id + " not found"));
    }

    public synchronized Optional<Student> byRegNo(String regNo) {
        return all().stream().filter(s -> regNo.equalsIgnoreCase(s.getRegNo())).findFirst();
    }

    public synchronized Student create(Student s) {
        // Uniqueness: regNo
        if (byRegNo(s.getRegNo()).isPresent()) {
            throw new ConflictException("regNo already exists: " + s.getRegNo());
        }
        List<Student> list = all();
        long id = Csv.nextId(list);
        s.setId(id);
        Csv.append(path, new String[]{
                String.valueOf(s.getId()),
                s.getRegNo(), s.getFirstName(), s.getLastName(),
                nullToEmpty(s.getDob()), nullToEmpty(s.getGender()), nullToEmpty(s.getContact()),
                nullToEmpty(s.getProgram()), s.getStatus()==null? "ACTIVE" : s.getStatus()
        });
        return s;
    }

    public synchronized Student update(long id, Student patch) {
        List<Student> list = all();
        boolean exists = false;

        // regNo uniqueness check if changed
        if (patch.getRegNo()!=null && !patch.getRegNo().isBlank()) {
            byRegNo(patch.getRegNo()).ifPresent(existing -> {
                if (existing.getId()!=id) throw new ConflictException("regNo already exists: " + patch.getRegNo());
            });
        }

        for (Student s : list) {
            if (s.getId()==id) {
                exists = true;
                if (patch.getRegNo()!=null && !patch.getRegNo().isBlank()) s.setRegNo(patch.getRegNo());
                if (patch.getFirstName()!=null) s.setFirstName(patch.getFirstName());
                if (patch.getLastName()!=null) s.setLastName(patch.getLastName());
                if (patch.getDob()!=null) s.setDob(patch.getDob());
                if (patch.getGender()!=null) s.setGender(patch.getGender());
                if (patch.getContact()!=null) s.setContact(patch.getContact());
                if (patch.getProgram()!=null) s.setProgram(patch.getProgram());
                if (patch.getStatus()!=null) s.setStatus(patch.getStatus());
            }
        }
        if (!exists) throw new NotFoundException("Student id " + id + " not found");
        Csv.writeAll(path, headers, list.stream().map(this::toRow).collect(Collectors.toList()));
        return byId(id);
    }

    public synchronized void delete(long id) {
        List<Student> list = all();
        boolean removed = list.removeIf(s -> s.getId()==id);
        if (!removed) throw new NotFoundException("Student id " + id + " not found");
        Csv.writeAll(path, headers, list.stream().map(this::toRow).collect(Collectors.toList()));
    }

    public synchronized List<Student> search(String q) {
        String qq = q==null ? "" : q.toLowerCase();
        return all().stream().filter(s ->
                s.getRegNo().toLowerCase().contains(qq) ||
                s.getFirstName().toLowerCase().contains(qq) ||
                s.getLastName().toLowerCase().contains(qq)
        ).collect(Collectors.toList());
    }

    private String[] toRow(Student s) {
        return new String[]{
                String.valueOf(s.getId()),
                s.getRegNo(), s.getFirstName(), s.getLastName(),
                nullToEmpty(s.getDob()), nullToEmpty(s.getGender()), nullToEmpty(s.getContact()),
                nullToEmpty(s.getProgram()), nullToEmpty(s.getStatus())
        };
    }
    private static String nullToEmpty(String s) { return s==null? "" : s; }
}

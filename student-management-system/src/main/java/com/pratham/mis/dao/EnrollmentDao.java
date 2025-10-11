package com.pratham.mis.dao;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVRecord;

import com.pratham.mis.ConflictException;
import com.pratham.mis.NotFoundException;
import com.pratham.mis.model.Enrollment;
import com.pratham.mis.util.Csv;

public class EnrollmentDao {
    private final String path;
    private final String[] headers = {"id","studentId","offeringId"};

    public EnrollmentDao(String path) { this.path = path; }

    private Enrollment map(CSVRecord r) {
        return new Enrollment(
                Long.parseLong(r.get("id")),
                Long.parseLong(r.get("studentId")),
                Long.parseLong(r.get("offeringId"))
        );
    }

    public synchronized List<Enrollment> all() { return Csv.readAll(path, this::map); }

    public synchronized Enrollment byId(long id) {
        return all().stream().filter(e -> e.getId()==id).findFirst()
                .orElseThrow(() -> new NotFoundException("Enrollment id " + id + " not found"));
    }

    public synchronized Enrollment create(Enrollment e) {
        // uniqueness (studentId, offeringId)
        boolean exists = all().stream().anyMatch(x -> x.getStudentId()==e.getStudentId() && x.getOfferingId()==e.getOfferingId());
        if (exists) throw new ConflictException("Enrollment exists for student " + e.getStudentId() + " offering " + e.getOfferingId());
        List<Enrollment> list = all();
        long id = Csv.nextId(list);
        e.setId(id);
        Csv.append(path, new String[]{String.valueOf(e.getId()), String.valueOf(e.getStudentId()), String.valueOf(e.getOfferingId())});
        return e;
    }

    public synchronized List<Enrollment> byStudent(long studentId) {
        return all().stream().filter(e -> e.getStudentId()==studentId).collect(Collectors.toList());
    }

    public synchronized List<Enrollment> byOffering(long offeringId) {
        return all().stream().filter(e -> e.getOfferingId()==offeringId).collect(Collectors.toList());
    }
}

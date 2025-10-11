package com.pratham.mis.dao;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVRecord;

import com.pratham.mis.ConflictException;
import com.pratham.mis.NotFoundException;
import com.pratham.mis.model.Course;
import com.pratham.mis.util.Csv;

public class CourseDao {
    private final String path;
    private final String[] headers = {"id","code","title","credits","departmentId"};

    public CourseDao(String path) { this.path = path; }

    private Course map(CSVRecord r) {
        String dept = r.get("departmentId");
        return new Course(
                Long.parseLong(r.get("id")),
                r.get("code"),
                r.get("title"),
                parseIntSafe(r.get("credits"), 3),
                dept==null || dept.isBlank()? null : Long.parseLong(dept)
        );
    }

    public synchronized List<Course> all() {
        return Csv.readAll(path, this::map);
    }

    public synchronized Course byId(long id) {
        return all().stream().filter(c -> c.getId()==id).findFirst()
                .orElseThrow(() -> new NotFoundException("Course id " + id + " not found"));
    }

    public synchronized Optional<Course> byCode(String code) {
        return all().stream().filter(c -> code.equalsIgnoreCase(c.getCode())).findFirst();
    }

    public synchronized Course create(Course c) {
        if (byCode(c.getCode()).isPresent()) throw new ConflictException("code exists: " + c.getCode());
        List<Course> list = all();
        long id = Csv.nextId(list);
        c.setId(id);
        Csv.append(path, new String[]{
                String.valueOf(c.getId()), c.getCode(), c.getTitle(), String.valueOf(c.getCredits()),
                c.getDepartmentId()==null? "" : String.valueOf(c.getDepartmentId())
        });
        return c;
    }

    public synchronized Course update(long id, Course patch) {
        List<Course> list = all();
        boolean exists = false;

        if (patch.getCode()!=null) {
            byCode(patch.getCode()).ifPresent(existing -> {
                if (existing.getId()!=id) throw new ConflictException("code exists: " + patch.getCode());
            });
        }

        for (Course c : list) {
            if (c.getId()==id) {
                exists = true;
                if (patch.getCode()!=null) c.setCode(patch.getCode());
                if (patch.getTitle()!=null) c.setTitle(patch.getTitle());
                if (patch.getCredits()!=0) c.setCredits(patch.getCredits());
                c.setDepartmentId(patch.getDepartmentId()); // Allow setting to null
            }
        }
        if (!exists) throw new NotFoundException("Course id " + id + " not found");
        Csv.writeAll(path, headers, list.stream().map(this::toRow).collect(Collectors.toList()));
        return byId(id);
    }

    public synchronized void delete(long id) {
        List<Course> list = all();
        boolean removed = list.removeIf(c -> c.getId()==id);
        if (!removed) throw new NotFoundException("Course id " + id + " not found");
        Csv.writeAll(path, headers, list.stream().map(this::toRow).collect(Collectors.toList()));
    }

    private String[] toRow(Course c) {
        return new String[]{
                String.valueOf(c.getId()), c.getCode(), c.getTitle(),
                String.valueOf(c.getCredits()),
                c.getDepartmentId()==null? "" : String.valueOf(c.getDepartmentId())
        };
    }

    private static int parseIntSafe(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception e) { return def; }
    }
}

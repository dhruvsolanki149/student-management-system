package com.pratham.mis.dao;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVRecord;

import com.pratham.mis.NotFoundException;
import com.pratham.mis.model.Offering;
import com.pratham.mis.util.Csv;

public class OfferingDao {
    private final String path;
    private final String[] headers = {"id","courseId","semester","academicYear","sectionId"};

    public OfferingDao(String path) { this.path = path; }

    private Offering map(CSVRecord r) {
        String sec = r.get("sectionId");
        return new Offering(
                Long.parseLong(r.get("id")),
                Long.parseLong(r.get("courseId")),
                Integer.parseInt(r.get("semester")),
                r.get("academicYear"),
                sec==null || sec.isBlank()? null : Long.parseLong(sec)
        );
    }

    public synchronized List<Offering> all() {
        return Csv.readAll(path, this::map);
    }

    public synchronized Offering byId(long id) {
        return all().stream().filter(o -> o.getId()==id).findFirst()
                .orElseThrow(() -> new NotFoundException("Offering id " + id + " not found"));
    }

    public synchronized Offering create(Offering o) {
        List<Offering> list = all();
        long id = Csv.nextId(list);
        o.setId(id);
        Csv.append(path, new String[]{
                String.valueOf(o.getId()), String.valueOf(o.getCourseId()),
                String.valueOf(o.getSemester()), o.getAcademicYear(),
                o.getSectionId()==null? "" : String.valueOf(o.getSectionId())
        });
        return o;
    }

    public synchronized Offering update(long id, Offering patch) {
        List<Offering> list = all();
        boolean exists = false;
        for (Offering o : list) {
            if (o.getId()==id) {
                exists = true;
                if (patch.getCourseId()!=0) o.setCourseId(patch.getCourseId());
                if (patch.getSemester()!=0) o.setSemester(patch.getSemester());
                if (patch.getAcademicYear()!=null) o.setAcademicYear(patch.getAcademicYear());
                o.setSectionId(patch.getSectionId()); // can set null
            }
        }
        if (!exists) throw new NotFoundException("Offering id " + id + " not found");
        Csv.writeAll(path, headers, list.stream().map(this::toRow).collect(Collectors.toList()));
        return byId(id);
    }

    public synchronized void delete(long id) {
        List<Offering> list = all();
        boolean removed = list.removeIf(o -> o.getId()==id);
        if (!removed) throw new NotFoundException("Offering id " + id + " not found");
        Csv.writeAll(path, headers, list.stream().map(this::toRow).collect(Collectors.toList()));
    }

    private String[] toRow(Offering o) {
        return new String[]{
                String.valueOf(o.getId()), String.valueOf(o.getCourseId()),
                String.valueOf(o.getSemester()), o.getAcademicYear(),
                o.getSectionId()==null? "" : String.valueOf(o.getSectionId())
        };
    }
}

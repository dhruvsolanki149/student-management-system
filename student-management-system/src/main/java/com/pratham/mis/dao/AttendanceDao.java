package com.pratham.mis.dao;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVRecord;

import com.pratham.mis.ConflictException;
import com.pratham.mis.NotFoundException;
import com.pratham.mis.model.AttendanceEntry;
import com.pratham.mis.model.AttendanceSession;
import com.pratham.mis.util.Csv;

public class AttendanceDao {
    private final String sessionPath;
    private final String entryPath;
    private final OfferingDao offeringDao;
    private final String[] sessionHeaders = {"id","offeringId","date","periodNo","takenBy"};
    private final String[] entryHeaders = {"id","sessionId","studentId","status","remarks"};

    public AttendanceDao(String sessionPath, String entryPath, OfferingDao offeringDao) {
        this.sessionPath = sessionPath; this.entryPath = entryPath;
        this.offeringDao = offeringDao;
    }

    private AttendanceSession mapSession(CSVRecord r) {
        String takenBy = r.get("takenBy");
        return new AttendanceSession(
                Long.parseLong(r.get("id")),
                Long.parseLong(r.get("offeringId")),
                r.get("date"),
                Integer.parseInt(r.get("periodNo")),
                takenBy==null || takenBy.isBlank()? null : Long.parseLong(takenBy)
        );
    }
    private AttendanceEntry mapEntry(CSVRecord r) {
        return new AttendanceEntry(
                Long.parseLong(r.get("id")),
                Long.parseLong(r.get("sessionId")),
                Long.parseLong(r.get("studentId")),
                r.get("status"),
                r.get("remarks")
        );
    }

    public synchronized List<AttendanceSession> sessions() { return Csv.readAll(sessionPath, this::mapSession); }
    public synchronized List<AttendanceEntry> entries() { return Csv.readAll(entryPath, this::mapEntry); }

    public synchronized AttendanceSession createSession(AttendanceSession s) {
        // validate offering exists
        offeringDao.byId(s.getOfferingId());
        // prevent duplicate session per (offeringId, date, periodNo)
        boolean exists = sessions().stream().anyMatch(x ->
                x.getOfferingId()==s.getOfferingId() && x.getDate().equals(s.getDate()) && x.getPeriodNo()==s.getPeriodNo());
        if (exists) throw new ConflictException("Session already exists for offering/date/period");
        long id = Csv.nextId(sessions());
        s.setId(id);
        Csv.append(sessionPath, new String[]{
                String.valueOf(s.getId()), String.valueOf(s.getOfferingId()), s.getDate(),
                String.valueOf(s.getPeriodNo()), s.getTakenBy()==null? "" : String.valueOf(s.getTakenBy())
        });
        return s;
    }

    public synchronized AttendanceSession sessionById(long id) {
        return sessions().stream().filter(s -> s.getId()==id).findFirst()
                .orElseThrow(() -> new NotFoundException("Session id " + id + " not found"));
    }

    public synchronized List<AttendanceEntry> upsertEntries(long sessionId, List<AttendanceEntry> newEntries) {
        // 1. Validate session exists
        sessionById(sessionId);
    
        // 2. Read all existing entries and partition them
        List<AttendanceEntry> allEntries = entries();
        Map<Long, List<AttendanceEntry>> entriesBySession = allEntries.stream()
                .collect(Collectors.groupingBy(AttendanceEntry::getSessionId));
    
        List<AttendanceEntry> sessionEntries = entriesBySession.getOrDefault(sessionId, new ArrayList<>());
        Map<Long, AttendanceEntry> sessionEntriesByStudent = sessionEntries.stream()
                .collect(Collectors.toMap(AttendanceEntry::getStudentId, e -> e));
    
        // 3. Process the new/updated entries
        // De-duplicate the incoming list, keeping the last entry per student
        Map<Long, AttendanceEntry> newEntriesByStudent = new LinkedHashMap<>();
        for (AttendanceEntry e : newEntries) {
            newEntriesByStudent.put(e.getStudentId(), e);
        }
    
        boolean needsRewrite = false;
        for (AttendanceEntry newEntry : newEntriesByStudent.values()) {
            if (!Set.of("P", "A", "L").contains(newEntry.getStatus())) {
                throw new IllegalArgumentException("Invalid status (P/A/L): " + newEntry.getStatus());
            }
    
            AttendanceEntry existingEntry = sessionEntriesByStudent.get(newEntry.getStudentId());
            if (existingEntry != null) { // Update existing entry
                existingEntry.setStatus(newEntry.getStatus());
                existingEntry.setRemarks(newEntry.getRemarks());
                needsRewrite = true;
            } else {
                // Insert new entry
                long nextId = Csv.nextId(allEntries);
                newEntry.setId(nextId);
                newEntry.setSessionId(sessionId);
                allEntries.add(newEntry); // Add to the master list for writing
                sessionEntries.add(newEntry); // Add to the session list for returning
            }
        }
    
        // 4. Write back to CSV only if necessary
        if (needsRewrite) {
            Csv.writeAll(entryPath, entryHeaders, allEntries.stream().map(this::toRow).collect(Collectors.toList()));
        } else { // If only new entries were added, we can just append
            List<AttendanceEntry> toAppend = newEntriesByStudent.values().stream()
                    .filter(e -> !sessionEntriesByStudent.containsKey(e.getStudentId()))
                    .toList();
            for (AttendanceEntry entry : toAppend) {
                Csv.append(entryPath, toRow(entry));
            }
        }
    
        return sessionEntries;
    }

    public synchronized Summary summaryForStudent(long studentId) {
        List<AttendanceEntry> list = entries().stream().filter(e -> e.getStudentId()==studentId).toList();
        long present = list.stream().filter(e -> "P".equals(e.getStatus())).count();
        int total = list.size();
        double percent = total == 0 ? 0.0 : (present * 100.0 / total);
        return new Summary((int)present, (int)total, Math.round(percent*100.0)/100.0);
    }

    private String[] toRow(AttendanceEntry e) {
        return new String[]{
                String.valueOf(e.getId()), String.valueOf(e.getSessionId()),
                String.valueOf(e.getStudentId()), e.getStatus(),
                e.getRemarks()==null? "" : e.getRemarks()
        };
    }

    public record Summary(int presentCount, int totalCount, double percent) {}
}

package com.pratham.mis.api;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pratham.mis.dao.AttendanceDao;
import com.pratham.mis.dao.EnrollmentDao;
import com.pratham.mis.model.AttendanceEntry;
import com.pratham.mis.model.AttendanceSession;
import com.pratham.mis.model.Enrollment;

import io.javalin.http.Context;

public class AttendanceApi {
    private final AttendanceDao dao;
    private final EnrollmentDao enrollmentDao;
    private final ObjectMapper om = new ObjectMapper();

    public AttendanceApi(AttendanceDao dao, EnrollmentDao enrollmentDao) {
        this.dao = dao; this.enrollmentDao = enrollmentDao;
    }

    public void createSession(Context ctx) throws Exception {
        AttendanceSession in = om.readValue(ctx.body(), AttendanceSession.class);
        require(in.getOfferingId()!=0, "offeringId");
        require(in.getDate()!=null && !in.getDate().isBlank(), "date");
        require(in.getPeriodNo()>=1, "periodNo >= 1");
        // validate offering exists
        // This check is now handled inside AttendanceDao.createSession
        ctx.json(dao.createSession(in));
    }

    public void upsertEntries(Context ctx) throws Exception {
        long sessionId = Long.parseLong(ctx.pathParam("sessionId"));
        List<AttendanceEntry> entries = om.readValue(ctx.body(), new TypeReference<>() {});

        // Ensure all students in the request are enrolled for the offering associated with the session.
        AttendanceSession session = dao.sessionById(sessionId);
        Set<Long> enrolledStudentIds = enrollmentDao.byOffering(session.getOfferingId())
                .stream()
                .map(Enrollment::getStudentId)
                .collect(Collectors.toSet());

        entries.forEach(entry -> require(enrolledStudentIds.contains(entry.getStudentId()), "Student " + entry.getStudentId() + " is not enrolled in this offering."));

        ctx.json(dao.upsertEntries(sessionId, entries));
    }

    public void summaryForStudent(Context ctx) {
        String studentIdParam = ctx.queryParam("studentId");
        require(studentIdParam != null && !studentIdParam.isBlank(), "studentId query parameter");
        long studentId = Long.parseLong(studentIdParam);
        ctx.json(dao.summaryForStudent(studentId));
    }

    private static void require(boolean ok, String msg) {
        if (!ok) throw new IllegalArgumentException("Invalid: " + msg);
    }
}

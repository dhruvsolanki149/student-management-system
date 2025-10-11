package com.pratham.mis.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pratham.mis.dao.EnrollmentDao;
import com.pratham.mis.dao.OfferingDao;
import com.pratham.mis.dao.StudentDao;
import com.pratham.mis.model.Enrollment;

import io.javalin.http.Context;

public class EnrollmentApi {
    private final EnrollmentDao dao;
    private final StudentDao studentDao;
    private final OfferingDao offeringDao;
    private final ObjectMapper om = new ObjectMapper();

    public EnrollmentApi(EnrollmentDao dao, StudentDao studentDao, OfferingDao offeringDao) {
        this.dao = dao; this.studentDao = studentDao; this.offeringDao = offeringDao;
    }

    public void create(Context ctx) throws Exception {
        JsonNode n = om.readTree(ctx.body());
        long studentId = n.path("studentId").asLong(0);
        long offeringId = n.path("offeringId").asLong(0);
        if (studentId==0 || offeringId==0) throw new IllegalArgumentException("studentId and offeringId required");
        // validate existence
        studentDao.byId(studentId);
        offeringDao.byId(offeringId);
        ctx.json(dao.create(new Enrollment(0, studentId, offeringId)));
    }

    public void listByQuery(Context ctx) {
        String studentParam = ctx.queryParam("studentId");
        String offeringParam = ctx.queryParam("offeringId");
        if (studentParam != null) {
            long studentId = Long.parseLong(studentParam);
            ctx.json(dao.byStudent(studentId));
        } else if (offeringParam != null) {
            long offeringId = Long.parseLong(offeringParam);
            ctx.json(dao.byOffering(offeringId));
        } else {
            ctx.json(dao.all());
        }
    }
}

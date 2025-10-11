package com.pratham.mis.api;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pratham.mis.dao.CourseDao;
import com.pratham.mis.dao.OfferingDao;
import com.pratham.mis.model.Offering;

import io.javalin.http.Context;

public class OfferingApi {
    private final OfferingDao dao;
    private final CourseDao courseDao;
    private final ObjectMapper om = new ObjectMapper();

    public OfferingApi(OfferingDao dao, CourseDao courseDao) {
        this.dao = dao; this.courseDao = courseDao;
    }

    public void list(Context ctx) {
        // (optional filters later)
        ctx.json(dao.all());
    }

    public void get(Context ctx) {
        long id = Long.parseLong(ctx.pathParam("id"));
        ctx.json(dao.byId(id));
    }

    public void create(Context ctx) throws Exception {
        Offering in = om.readValue(ctx.body(), Offering.class);
        require(in.getCourseId() != 0, "courseId");
        require(in.getSemester() >= 1 && in.getSemester() <= 8, "semester 1..8");
        require(in.getAcademicYear() != null && !in.getAcademicYear().isBlank(), "academicYear");
        // validate course exists
        courseDao.byId(in.getCourseId());
        ctx.json(dao.create(in));
    }

    public void update(Context ctx) throws Exception {
        long id = Long.parseLong(ctx.pathParam("id"));
        Offering patch = om.readValue(ctx.body(), Offering.class);
        ctx.json(dao.update(id, patch));
    }

    public void delete(Context ctx) {
        long id = Long.parseLong(ctx.pathParam("id"));
        dao.delete(id);
        ctx.json(Map.of("deleted", id));
    }

    private static void require(boolean ok, String msg) {
        if (!ok) throw new IllegalArgumentException("Invalid: " + msg);
    }
}

package com.pratham.mis.api;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pratham.mis.dao.CourseDao;
import com.pratham.mis.model.Course;

import io.javalin.http.Context;

public class CourseApi {
    private final CourseDao dao;
    private final ObjectMapper om = new ObjectMapper();

    public CourseApi(CourseDao dao) { this.dao = dao; }

    public void list(Context ctx) { ctx.json(dao.all()); }
    public void get(Context ctx) {
        long id = Long.parseLong(ctx.pathParam("id"));
        ctx.json(dao.byId(id));
    }

    public void create(Context ctx) throws Exception {
        Course in = om.readValue(ctx.body(), Course.class);
        require(in.getCode(), "code");
        require(in.getTitle(), "title");
        if (in.getCredits()==0) in.setCredits(3);
        ctx.json(dao.create(in));
    }

    public void update(Context ctx) throws Exception {
        long id = Long.parseLong(ctx.pathParam("id"));
        Course patch = om.readValue(ctx.body(), Course.class);
        ctx.json(dao.update(id, patch));
    }

    public void delete(Context ctx) {
        long id = Long.parseLong(ctx.pathParam("id"));
        dao.delete(id);
        ctx.json(Map.of("deleted", id));
    }

    private static void require(String s, String field) {
        if (s==null || s.isBlank()) throw new IllegalArgumentException("Missing field: " + field);
    }
}

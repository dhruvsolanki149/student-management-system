package com.pratham.mis.api;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pratham.mis.dao.StudentDao;
import com.pratham.mis.model.Student;

import io.javalin.http.Context;

public class StudentApi {
    private final StudentDao dao;
    private final ObjectMapper om = new ObjectMapper();

    public StudentApi(StudentDao dao) { this.dao = dao; }

    public void list(Context ctx) {
        String q = ctx.queryParam("q"); // Get the query parameter, which can be null
        List<Student> result = (q == null || q.isBlank() ? dao.all() : dao.search(q)); // Handle null or blank
        ctx.json(result);
    }

    public void get(Context ctx) {
        long id = Long.parseLong(ctx.pathParam("id"));
        ctx.json(dao.byId(id));
    }

    public void create(Context ctx) throws Exception {
        Student in = om.readValue(ctx.body(), Student.class);
        require(in.getRegNo(), "regNo");
        require(in.getFirstName(), "firstName");
        require(in.getLastName(), "lastName");
        if (in.getStatus()==null || in.getStatus().isBlank()) in.setStatus("ACTIVE");
        ctx.json(dao.create(in));
    }

    public void update(Context ctx) throws Exception {
        long id = Long.parseLong(ctx.pathParam("id"));
        Student patch = om.readValue(ctx.body(), Student.class);
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

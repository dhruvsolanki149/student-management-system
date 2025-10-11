package com.pratham.mis;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import com.pratham.mis.api.AttendanceApi;
import com.pratham.mis.api.CourseApi;
import com.pratham.mis.api.EnrollmentApi;
import com.pratham.mis.api.OfferingApi;
import com.pratham.mis.api.StudentApi;
import com.pratham.mis.dao.AttendanceDao;
import com.pratham.mis.dao.CourseDao;
import com.pratham.mis.dao.EnrollmentDao;
import com.pratham.mis.dao.OfferingDao;
import com.pratham.mis.dao.StudentDao;
import com.pratham.mis.util.Http;

import io.javalin.Javalin;

public class App {
    public static void main(String[] args) throws Exception {
        ensureDataFiles();

        // DAOs
        var studentDao = new StudentDao("data/students.csv");
        var courseDao = new CourseDao("data/courses.csv");
        var offeringDao = new OfferingDao("data/offerings.csv");
        var enrollmentDao = new EnrollmentDao("data/enrollments.csv");
        var attendanceDao = new AttendanceDao("data/attendance_sessions.csv", "data/attendance_entries.csv", offeringDao);

        // APIs
        var studentApi = new StudentApi(studentDao);
        var courseApi = new CourseApi(courseDao);
        var offeringApi = new OfferingApi(offeringDao, courseDao);
        var enrollmentApi = new EnrollmentApi(enrollmentDao, studentDao, offeringDao);
        var attendanceApi = new AttendanceApi(attendanceDao, enrollmentDao);

        Javalin app = Javalin.create(cfg -> {
            cfg.bundledPlugins.enableRouteOverview("/routes");
            cfg.http.defaultContentType = "application/json";
            cfg.staticFiles.add("/public"); // Serve static files from the /public classpath folder
        });

        // Global error handlers
        app.exception(IllegalArgumentException.class, (e, ctx) -> Http.badRequest(ctx, e.getMessage()));
        app.exception(NotFoundException.class, (e, ctx) -> Http.notFound(ctx, e.getMessage()));
        app.exception(ConflictException.class, (e, ctx) -> Http.conflict(ctx, e.getMessage()));
        app.exception(Exception.class, (e, ctx) -> {
            e.printStackTrace();
            Http.serverError(ctx, "Unexpected error");
        });

        // Health
        app.get("/api/health", ctx -> ctx.json(new SimpleMsg("ok")));

        // Students
        app.get("/api/students", studentApi::list);
        app.get("/api/students/{id}", studentApi::get);
        app.post("/api/students", studentApi::create);
        app.put("/api/students/{id}", studentApi::update);
        app.delete("/api/students/{id}", studentApi::delete);

        // Courses
        app.get("/api/courses", courseApi::list);
        app.get("/api/courses/{id}", courseApi::get);
        app.post("/api/courses", courseApi::create);
        app.put("/api/courses/{id}", courseApi::update);
        app.delete("/api/courses/{id}", courseApi::delete);

        // Offerings
        app.get("/api/offerings", offeringApi::list);
        app.get("/api/offerings/{id}", offeringApi::get);
        app.post("/api/offerings", offeringApi::create);
        app.put("/api/offerings/{id}", offeringApi::update);
        app.delete("/api/offerings/{id}", offeringApi::delete);

        // Enrollments
        app.post("/api/enrollments", enrollmentApi::create);
        app.get("/api/enrollments", enrollmentApi::listByQuery);

        // Attendance
        app.post("/api/attendance/sessions", attendanceApi::createSession);
        app.post("/api/attendance/{sessionId}/entries", attendanceApi::upsertEntries);
        app.get("/api/attendance/summary", attendanceApi::summaryForStudent);

        app.start(8080);
        System.out.println("College MIS (CSV) running on http://localhost:8080  |  Routes → /routes");
    }

    record SimpleMsg(String status) {}

    private static void ensureDataFiles() throws Exception {
        Files.createDirectories(Path.of("data"));
        touchWithHeader("data/students.csv", "id,regNo,firstName,lastName,dob,gender,contact,program,status");
        touchWithHeader("data/courses.csv", "id,code,title,credits,departmentId");
        touchWithHeader("data/offerings.csv", "id,courseId,semester,academicYear,sectionId");
        touchWithHeader("data/enrollments.csv", "id,studentId,offeringId");
        touchWithHeader("data/attendance_sessions.csv", "id,offeringId,date,periodNo,takenBy");
        touchWithHeader("data/attendance_entries.csv", "id,sessionId,studentId,status,remarks");
    }

    private static void touchWithHeader(String path, String header) throws Exception {
        File f = new File(path);
        if (!f.exists()) {
            Files.writeString(Path.of(path), header + System.lineSeparator());
        }
    }
}

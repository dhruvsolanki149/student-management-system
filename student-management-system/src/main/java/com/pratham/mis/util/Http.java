package com.pratham.mis.util;

import java.util.Map;

import io.javalin.http.Context;

public class Http {
    public static void badRequest(Context ctx, String msg) {
        ctx.status(400).json(Map.of("error", msg, "status", 400));
    }
    public static void notFound(Context ctx, String msg) {
        ctx.status(404).json(Map.of("error", msg, "status", 404));
    }
    public static void conflict(Context ctx, String msg) {
        ctx.status(409).json(Map.of("error", msg, "status", 409));
    }
    public static void serverError(Context ctx, String msg) {
        ctx.status(500).json(Map.of("error", msg, "status", 500));
    }
}


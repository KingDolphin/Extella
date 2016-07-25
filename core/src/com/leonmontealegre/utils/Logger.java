package com.leonmontealegre.utils;

public class Logger {

    public static boolean debug = true;

    public static void log(Object x, boolean singleLine) {
        if (!debug) return;

        System.out.print(x + (singleLine ? "" : "\n"));
    }

    public static void log(Object x) {
        log(x, false);
    }

    public static void logf(String format, Object... args) {
        if (!debug) return;

        System.out.printf(format, args);
    }

    public static void logErr(Object x) {
        if (!debug) return;

        System.err.println(x);
    }

    public static void logList(Object... args) {
        if (!debug) return;

        int i;
        for (i = 0; i < args.length-1; i++)
            System.out.print(args[i] + ", ");
        System.out.println(args[i]);
    }

}
package com.trolltech.qt.internal;

public class Reporter {

    public void report(String a) {
        if (!report) return;
        buffer.append(a);
        buffer.append('\n');
    }

    public void report(String a, String b) {
        if (!report) return;
        buffer.append(a);
        buffer.append(b);
        buffer.append('\n');
    }

    public void report(String a, String b, String c) {
        if (!report) return;
        buffer.append(a);
        buffer.append(b);
        buffer.append(c);
        buffer.append('\n');
    }

    public void report(String a, String b, String c, String d) {
        if (!report) return;
        buffer.append(a);
        buffer.append(b);
        buffer.append(c);
        buffer.append(d);
        buffer.append('\n');
    }

    public void report(String a, String b, String c, String d, String e) {
        if (!report) return;
        buffer.append(a);
        buffer.append(b);
        buffer.append(c);
        buffer.append(d);
        buffer.append(e);
        buffer.append('\n');
    }

    public void report(String a, String b, String c, String d, String e, String f) {
        if (!report) return;
        buffer.append(a);
        buffer.append(b);
        buffer.append(c);
        buffer.append(d);
        buffer.append(e);
        buffer.append(f);
        buffer.append('\n');
    }

    public void report(String a, String b, String c, String d, String e, String f, String g) {
        if (!report) return;
        buffer.append(a);
        buffer.append(b);
        buffer.append(c);
        buffer.append(d);
        buffer.append(e);
        buffer.append(f);
        buffer.append(g);
        buffer.append('\n');
    }

    public void report(String a, String b, String c, String d, String e, String f, String g, String h) {
        if (!report) return;
        buffer.append(a);
        buffer.append(b);
        buffer.append(c);
        buffer.append(d);
        buffer.append(e);
        buffer.append(f);
        buffer.append(g);
        buffer.append(h);
        buffer.append('\n');
    }

    public String toString() {
        return buffer.toString();
    }

    public void setReportEnabled(boolean e) {
        report = e;
    }

    public boolean isReportEnabled() {
        return report;
    }

    private boolean report = true;
    private StringBuffer buffer = new StringBuffer();
}
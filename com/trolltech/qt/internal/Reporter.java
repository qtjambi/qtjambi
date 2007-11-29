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
        return (old != null) ? old.toString() + "\n" + buffer.toString() : buffer.toString();
    }

    public void setReportEnabled(boolean e) {
        report = e;
    }

    public boolean isReportEnabled() {
        return report;
    }

    /**
     * Extracts the last batch of reports made since the last time
     * this function was called.
     * @returns The last batch of reprts...
     */
    public String recentReports() {
        String batch = buffer.toString();
        if (old == null)
            old = buffer;
        else
            old.append(buffer);
        buffer = new StringBuilder();
        return batch;
    }

    private boolean report = true;
    private StringBuilder buffer = new StringBuilder();
    private StringBuilder old;
}
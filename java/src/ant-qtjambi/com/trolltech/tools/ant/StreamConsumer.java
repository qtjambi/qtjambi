package com.trolltech.tools.ant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class StreamConsumer extends Thread {

    public StreamConsumer(InputStream in, PrintStream out) {
        this.in = in;
        this.out = out;
    }
                                        
    @Override
    public void run() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        try {
            while ( (line = reader.readLine()) != null) {
                if (out != null)
                    out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private PrintStream out;
    private InputStream in;
}
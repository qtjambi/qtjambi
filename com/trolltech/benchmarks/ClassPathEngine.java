package com.trolltech.benchmarks;

import com.trolltech.qt.core.*;
import java.util.*;

public class ClassPathEngine {
     public static void main(String args[]) {
        long t1 = System.currentTimeMillis();

        QDir dir = new QDir("classpath:com/trolltech/qt/gui");
        List<String> names = dir.entryList();
        for (int i=0; i<names.size(); ++i) {
            QFileInfo info = new QFileInfo(dir.absoluteFilePath(names.get(i)));
            //System.out.println(info.absoluteFilePath());
        }

        long t2 = System.currentTimeMillis();

        System.out.printf("Creating %d fileinfos took: %d\n", names.size(), t2 - t1);

    }
}

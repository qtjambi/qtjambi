/*************************************************************************
 *
 * Copyright (C) 1992-$THISYEAR$ $TROLLTECH$. All rights reserved.
 *
 * This file is part of $PRODUCT$.
 *
 * $JAVA_LICENSE$
 *
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 * WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 *
 **************************************************************************/

package com.trolltech.tools;

import com.trolltech.qt.internal.*;

import java.util.*;
import java.io.*;

public class CacheCleaner {

    private static void doHelp() {
        System.out.println("");
        System.out.println("CacheCleaner\nA utility for cleaning up the Qt Jambi native library cache\n");
        System.out.println("USAGE:");
        System.out.println("  > java " + CacheCleaner.class.getName() + " [options] [list of keys]\n");
        System.out.println("  Options: ");
        System.out.println("    --all    -a      Removes all keys from the cache");
        System.out.println("    --list   -l      Lists used entries in the cache");
        System.out.println("    --help   -h      This help");
        System.out.println("\n\n");
    }

    public static void main(String args[]) throws Exception {

        boolean cleanAll = false;
        boolean list = false;
        List<String> names = new ArrayList<String>();

        for (String arg : args) {
            if (arg.equals("--all") || arg.equals("-a")) {
                cleanAll = true;
            } else if (arg.equals("--list") || arg.equals("-l")) {
                list = true;
            } else if (arg.equals("--help") || arg.equals("-h")) {
                doHelp();
                return;
            } else {
                names.add(arg);
            }
        }

        if (names.size() == 0 && !list && !cleanAll) {
            doHelp();
            list = true;
        }


        if (list) {
            System.out.println("Qt Jambi Caches:");
            int cutpoint = baseName().length();
            for (File d : cacheDirs()) {
                System.out.println("    \"" + d.getName().substring(cutpoint) + "\"");
            }
        } else {
            File dirs[];

            if (cleanAll) {
                dirs = cacheDirs();
            } else {
                dirs = new File[names.size()];
                for (int i=0; i<names.size(); ++i) {
                    dirs[i] = NativeLibraryManager.jambiTempDirBase(names.get(i));
                    if (!dirs[i].exists())
                        throw new FileNotFoundException(dirs[i].toString());
                }
            }

            for (File d : dirs) {
                System.out.println("Deleting \"" + d.getAbsolutePath() + "\"");
                delete(d);
            }
        }
    }

    private static void delete(File d) throws Exception {
        if (d.isDirectory()) {
            File files[] = d.listFiles();
            for (File f : files)
                delete(f);
        }
        System.out.print(" - deleting: " + d.getAbsolutePath() + "...");
        d.delete();   
        System.out.println("ok!");
    }

    private static String baseName() {
        return NativeLibraryManager.jambiTempDirBase("").getName();
    }

    private static File[] cacheDirs() {
        File tmpDirBase = NativeLibraryManager.jambiTempDirBase("");
        File tmpDir = tmpDirBase.getParentFile();
        final String baseName = tmpDirBase.getName();
        File dirs[] = tmpDir.listFiles(new FileFilter() {
            public boolean accept(File path) {
                return path.isDirectory() && path.getName().startsWith(baseName);
            }
        });
        return dirs;
    }
    
}

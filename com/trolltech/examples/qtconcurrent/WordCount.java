package com.trolltech.examples.qtconcurrent;

import java.util.*;
import com.trolltech.qt.core.*;

public class WordCount implements QtConcurrent.MappedFunctor<HashMap<String, Integer>, String>,
                                  QtConcurrent.ReducedFunctor<HashMap<String, Integer>, HashMap<String, Integer>> {
    private static List<String >findFiles(String startDir, List<String> filters) {
        List<String> names = new ArrayList<String>();
        QDir dir = new QDir(startDir);

        for (String file : dir.entryList(filters, new QDir.Filters(QDir.Filter.Files)))
            names.add(startDir + "/" + file);

        for (String subdir : dir.entryList(new QDir.Filters(QDir.Filter.AllDirs, QDir.Filter.NoDotAndDotDot)))
            names.addAll(findFiles(startDir + "/" + subdir, filters));
        return names;
    }

    /*
        Single threaded word counter function.
    */
    static HashMap<String, Integer> singleThreadedWordCount(List<String> files) {
        HashMap<String, Integer> wordCount = new HashMap<String,Integer>();

        for (String file : files) {
            QFile f = new QFile(file);
            f.open(QIODevice.OpenModeFlag.ReadOnly);
            QTextStream textStream = new QTextStream(f);
            while (!textStream.atEnd()) {
                for (String word : textStream.readLine().split(" ")) {
                    int i = wordCount.containsKey(word) ? wordCount.get(word) : 0;
                    wordCount.put(word, i + 1);
                }
            }

            f.close();
        }

        return wordCount;
    }


//     countWords counts the words in a single file. This function is
//     called in parallel by several threads and must be thread
//     safe.
    public HashMap<String, Integer> map(String file) {
        QFile f = new QFile(file);
        f.open(QIODevice.OpenModeFlag.ReadOnly);
        QTextStream textStream = new QTextStream(f);
        HashMap<String, Integer> wordCount = new HashMap<String, Integer>();

        while (!textStream.atEnd()) {
            for (String word : textStream.readLine().split(" ")) {
                int i = wordCount.containsKey(word) ? wordCount.get(word) : 0;
                wordCount.put(word, i + 1);
            }
        }

        return wordCount;
    }

//     reduce adds the results from map to the final
//     result. This functor will only be called by one thread
//     at a time.
    public void reduce(HashMap<String, Integer> result, HashMap<String, Integer> w) {
        Set<String> keys = w.keySet();
        for (String key : keys) {
            int i = result.containsKey(key) ? result.get(key) : 0;
            result.put(key, i + w.get(key));
        }
    }

    public HashMap<String, Integer> defaultResult() {
        return new HashMap<String, Integer>();
    }

    public static void main(String args[])
    {
        System.out.println("finding files...");

        List<String> filters = new ArrayList<String>();
        filters.add("*.java");

        List<String> files = findFiles("classpath:com/trolltech/examples", filters);
        System.out.println(files.size() + " files");

        System.out.println("warmup");
        {
            QTime time = new QTime();
            time.start();
            singleThreadedWordCount(files);
        }

        System.out.println("warmup done");

        int singleThreadTime = 0;
        {
            QTime time = new QTime();
            time.start();
            singleThreadedWordCount(files);
            singleThreadTime = time.elapsed();
            System.out.println("single thread: " + singleThreadTime);
        }

        int mapReduceTime = 0;
        {
            QTime time = new QTime();
            time.start();
            WordCount wc = new WordCount();
            QFuture<HashMap<String, Integer>> total = QtConcurrent.mappedReduced(files, wc, wc);
            total.waitForFinished();
            mapReduceTime = time.elapsed();
            System.out.println("MapReduce: " + mapReduceTime);
        }
        System.out.println("MapReduce speedup x " + ((double)singleThreadTime - (double)mapReduceTime) / (double)mapReduceTime + 1);
    }

}

package com.trolltech.autotests;

import com.trolltech.qt.core.*;

import static org.junit.Assert.*;
import org.junit.*;

import java.util.*;

public class Concurrent extends QApplicationTest {
    
    private static final int COUNT = 100;
    
    static class MutableString {
        public String value;
        
        public MutableString(String value) { this.value = value; }
        
        @Override 
        public boolean equals(Object other) {
            if (other instanceof MutableString)
                return ((MutableString)other).value.equals(value);
            else if (other instanceof String)
                return value.equals((String) other);
            else
                return super.equals(value);
        }
    }
    
    @Test
    public void testMap() {
        List<MutableString> strings = new ArrayList<MutableString>();
        
        for (int i=0; i<COUNT; ++i)
            strings.add(new MutableString("" + i));
        
        QFutureVoid future = QtConcurrent.map(strings, 
                new QtConcurrent.MapFunctor<MutableString>() {

                    public void map(MutableString object) {                       
                        object.value += " foobar";                                               
                    }                                           
                }        
        );
        
        future.waitForFinished();
        for (int i=0; i<COUNT; ++i)
            assertEquals("" + i + " foobar", strings.get(i).value);
    }
    
    @Test
    public void testBlockingMap() {
        List<MutableString> strings = new ArrayList<MutableString>();
        
        for (int i=0; i<COUNT; ++i)
            strings.add(new MutableString("" + i));
        
        QtConcurrent.blockingMap(strings, 
                new QtConcurrent.MapFunctor<MutableString>() {

                    public void map(MutableString object) {                       
                        object.value += " foobar";                                               
                    }                                           
                }        
        );
        
        for (int i=0; i<COUNT; ++i)
            assertEquals("" + i + " foobar", strings.get(i).value);        
    }
    
    @Test
    public void testMapped() {
        List<String> strings = new ArrayList<String>();        
        for (int i=0; i<COUNT; ++i)
            strings.add("" + (i*i));
        
        QFuture<Integer> future = QtConcurrent.mapped(strings,
                new QtConcurrent.MappedFunctor<Integer, String>() {
                    public Integer map(String object) {
                        return Integer.parseInt(object);
                    }
                }
        );
        
        future.waitForFinished();
        List<Integer> results = future.results();
        assertEquals(COUNT, results.size());
        
        for (int i=0; i<results.size(); ++i)
            assertEquals(i*i, results.get(i));
    }
    
    @Test
    public void testBlockingMapped() {
        List<String> strings = new ArrayList<String>();        
        for (int i=0; i<COUNT; ++i)
            strings.add("" + (i*i));
        
        List<Integer> results = QtConcurrent.blockingMapped(strings,
                new QtConcurrent.MappedFunctor<Integer, String>() {
                    public Integer map(String object) {
                        return Integer.parseInt(object);
                    }
                }
        );
        
        assertEquals(COUNT, results.size());        
        for (int i=0; i<results.size(); ++i)
            assertEquals(i*i, results.get(i));        
    }
    
    @Test
    public void testMappedReduced() {
        List<Integer> strings = new ArrayList<Integer>();        
        for (int i=0; i<COUNT; ++i)
            strings.add(i);

        QFuture<MutableString> future = QtConcurrent.mappedReduced(strings, 
                new QtConcurrent.MappedFunctor<String, Integer>() {
                    public String map(Integer i) {
                        return ((Integer)(i*i)).toString();
                    }
                }, 
                new QtConcurrent.ReducedFunctor<MutableString, String>() {
                    public MutableString defaultResult() {
                        return new MutableString("5");
                    }
                    
                    public void reduce(MutableString result, String n) {
                        int i = Integer.parseInt(result.value) + Integer.parseInt(n);
                        result.value = new Integer(i).toString();
                    }
                }
        );
        
        int n=5;
        for (int i=0; i<COUNT;++i)
            n += i*i;
        
        future.waitForFinished();
        assertEquals(1, future.resultCount());
        assertEquals(n, Integer.parseInt(future.result().value));
    }

}

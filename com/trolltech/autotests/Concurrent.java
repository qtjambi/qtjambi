package com.trolltech.autotests;

import com.trolltech.qt.core.*;

import static org.junit.Assert.*;
import org.junit.*;

import java.lang.reflect.Method;
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

    @Test
    public void testBlockingMappedReduced() {
        List<Integer> strings = new ArrayList<Integer>();        
        for (int i=0; i<COUNT; ++i)
            strings.add(i);

        MutableString result = QtConcurrent.blockingMappedReduced(strings, 
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
        
        assertEquals(n, Integer.parseInt(result.value));
    }
    
    @Test
    public void testFiltered() {
        List<Integer> ints = new ArrayList<Integer>();        
        for (int i=0; i<COUNT*2; ++i)
            ints.add(i);

        QFuture<Integer> future = QtConcurrent.filtered(ints, 
                new QtConcurrent.FilteredFunctor<Integer>() {
                    public boolean filter(Integer i) {
                        return (i >= COUNT);
                    }
                }
        );
        
        future.waitForFinished();
        assertEquals(COUNT, future.resultCount());
        
        List<Integer> lst = future.results();
        for (int i=0; i<future.resultCount(); ++i)
            assertEquals(i+COUNT, lst.get(i));
    }
    
    @Test
    public void testBlockingFiltered() {
        List<Integer> ints = new ArrayList<Integer>();        
        for (int i=0; i<COUNT*2; ++i)
            ints.add(i);

        List<Integer> lst = QtConcurrent.blockingFiltered(ints, 
                new QtConcurrent.FilteredFunctor<Integer>() {
                    public boolean filter(Integer i) {
                        return (i >= COUNT);
                    }
                }
        );
        
        assertEquals(COUNT, lst.size());        
        for (int i=0; i<lst.size(); ++i)
            assertEquals(i+COUNT, lst.get(i));
    }

    
    static class MutableInteger {
        public int value;
        
        public MutableInteger(int value) { this.value = value; }
    }
    
    @Test
    public void testFilteredReduced() {
        List<Integer> ints = new ArrayList<Integer>();        
        for (int i=0; i<COUNT*2; ++i)
            ints.add(i);

        QFuture<MutableInteger> future = QtConcurrent.filteredReduced(ints, 
                new QtConcurrent.FilteredFunctor<Integer>() {
                    public boolean filter(Integer i) {
                        return (i >= COUNT);
                    }
                },
                new QtConcurrent.ReducedFunctor<MutableInteger, Integer>() {
                    public MutableInteger defaultResult() {
                        return new MutableInteger(3);
                    }
                    
                    public void reduce(MutableInteger result, Integer intermediate) {
                        result.value += intermediate;
                    }
                }
        );
        
        future.waitForFinished();
        assertEquals(1, future.resultCount());

        int n=3;
        for (int i=COUNT; i<COUNT*2; ++i)
            n += i;
        
        assertEquals(n, future.result().value);
        
    }
    
    @Test
    public void testBlockingFilteredReduced() {
        List<Integer> ints = new ArrayList<Integer>();        
        for (int i=0; i<COUNT*2; ++i)
            ints.add(i);

        MutableInteger result = QtConcurrent.blockingFilteredReduced(ints, 
                new QtConcurrent.FilteredFunctor<Integer>() {
                    public boolean filter(Integer i) {
                        return (i >= COUNT);
                    }
                },
                new QtConcurrent.ReducedFunctor<MutableInteger, Integer>() {
                    public MutableInteger defaultResult() {
                        return new MutableInteger(3);
                    }
                    
                    public void reduce(MutableInteger result, Integer intermediate) {
                        result.value += intermediate;
                    }
                }
                
        );
        
        int n=3;
        for (int i=COUNT; i<COUNT*2; ++i)
            n += i;
        
        assertEquals(n, result.value);
    }
    
    public void method(MutableInteger i) {
        i.value += 123;
    }
    
    public Integer method2(Integer i) {
        return i + 1234;
    }
    
    @Test
    public void testRunVoid() {
        Method m = null;    
        try {
            m = Concurrent.class.getMethod("method", MutableInteger.class);
        } catch (Exception e) { }
                
        assertTrue(m != null);
        
        MutableInteger i = new MutableInteger(321);
        QFutureVoid v = QtConcurrent.runVoidMethod(this, m, i);
        
        v.waitForFinished();
        assertEquals(444, i.value);
    }
    
    @Test
    public void testRun() {
        Method m = null;
        try {
            m = Concurrent.class.getMethod("method2", Integer.class);
        } catch (Exception e) { }
        
        assertTrue(m != null);
        
        QFuture<Integer> future = QtConcurrent.run(this, m, 4321);
        
        future.waitForFinished();
        assertEquals(5555, future.result());
    }
    
    public int method3(int a, byte b, short c, float d) {
        return (int) (a + b + c + d);
    }
    
    @Test
    public void testRunWithPrimitiveTypes() {
        Method m = null;
        try {
            m = Concurrent.class.getMethod("method3", Integer.TYPE, Byte.TYPE, Short.TYPE, Float.TYPE);
        } catch (Exception e) {}
        
        assertTrue(m != null);
        
        QFuture<Integer> future = QtConcurrent.run(this, m, 1, 2, 3, 4);
        
        future.waitForFinished();
        assertEquals(10, future.result());        
    }
    
    public void method4(MutableInteger a, int b, int c) {
        a.value = b + c;
    }
    
    @Test
    public void testRunVoidWithPrimitiveTypes() {
        Method m = null;
        try {
            m = Concurrent.class.getMethod("method4", MutableInteger.class, Integer.TYPE, Integer.TYPE);
        } catch (Exception e) {}
        
        assertTrue(m != null);        
        
        MutableInteger i = new MutableInteger(0);
        QFutureVoid future = QtConcurrent.runVoidMethod(this, m, i, 123, 321);
        
        future.waitForFinished();
        assertEquals(444, i.value);        
    }
    
    @Test
    public void testResultAt() {
        List<Integer> ints = new ArrayList<Integer>();
        for (int i=0; i<COUNT*2; ++i)
            ints.add(i);
        
        {
            QFuture<Integer> result = QtConcurrent.mapped(ints,
                    new QtConcurrent.MappedFunctor<Integer, Integer>() {
                        public Integer map(Integer i) {
                            return i + 1;
                        }
                    }
            );
            
            result.waitForFinished();
            assertEquals(COUNT*2, result.resultCount());
            for (int i=0; i<result.resultCount(); ++i)
                assertEquals(i+1, result.resultAt(i));
        }
                
        {    
            QFuture<Integer> future = QtConcurrent.filtered(ints, 
                    new QtConcurrent.FilteredFunctor<Integer>() {
                        public boolean filter(Integer i) {
                            return (i >= COUNT);
                        }
                    }
            );
        
            future.waitForFinished();
            assertEquals(COUNT, future.resultCount());            
            for (int i=0; i<future.resultCount(); ++i)
                assertEquals(i+COUNT, future.resultAt(i));
        }
        
    }
    
}

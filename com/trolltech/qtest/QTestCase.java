/****************************************************************************
**
** Copyright (C) 1992-$THISYEAR$ $TROLLTECH$. All rights reserved.
**
** This file is part of $PRODUCT$.
**
** $JAVA_LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

package com.trolltech.qtest;

import java.util.*;
import java.io.*;
import java.lang.reflect.*;

import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.core.QObject;

class QTestFilter implements FilenameFilter
{
    private String m_fileName;
    
    public QTestFilter(String fileName)
    {
        m_fileName = fileName;
    }
    
    public boolean accept(File dir, String name)
    {
        return (name.equals(m_fileName));    
    }
}

class QTestData
{
    public String name;
    public Object[] dataSet;
}

public class QTestCase
{
    private LinkedList<String> m_searchPaths = new LinkedList<String>();
    private LinkedList<QTestException> m_listExceptions = new LinkedList<QTestException>();           
    private HashMap<String, Integer> m_indexMap = new HashMap<String, Integer>();
    private Class[] m_currentDataStructure = null;
    private LinkedList<QTestData> m_dataSets = new LinkedList<QTestData>();    
    private QTestData m_currentDataSet = null;
            
    public QTestCase()
    {
        this(true);
    }
    
    public QTestCase(boolean addSearchPaths)
    {
        if (addSearchPaths) {        
            addSearchPath(System.getProperty("user.dir"), false);
            
            Class c = getClass();
            String packageName = c.getPackage().getName();
            addSearchPath(System.getProperty("user.dir") + File.separatorChar + packageName.replace('.', File.separatorChar), 
                    false);
            
            String classPath = System.getProperty("java.class.path");
            StringTokenizer tokenizer = new StringTokenizer(classPath, File.pathSeparator);
            
            while (tokenizer.hasMoreTokens()) {
                 String token = tokenizer.nextToken();
                 addSearchPath(token, false);
                 addSearchPath(token + File.separatorChar + packageName.replace('.', File.separatorChar), false);

            }
         }
    }        
    
    /** Convenience functions for getting data types **/
    protected Class String() { return String.class; }    
    protected Class Object() { return Object.class; }
    protected Class Long() { return Long.class; }
    protected Class Integer() { return Integer.class; }
    protected Class Double() { return Double.class; }
    protected Class Character() { return Character.class; }
    protected Class Float() { return Float.class; }
    protected Class Short() { return Short.class; }
    protected Class Boolean() { return Boolean.class; }
    
    protected Class ObjectArray()
    {
        return (new Object[1]).getClass();
    }
    
    protected Class QObjectArray()
    {
        return (new QObject[1]).getClass();
    }
    
    protected Class ClassArray()
    {
        return (new Class[1].getClass());
    }
    
    private Class primitive(Class c)
    {
        Class c2;
        try {                
            Field f;
            f = c.getField("TYPE");
            c2 = (Class) f.get(null);                    
        } catch (Exception e) {
            c2 = null;
        }
        
        return c2;                        
    }
    
    
    protected Class _int()
    {
        return primitive(Integer());
    }
    
    protected Class _double()
    {
        return primitive(Double());
    }
    
    protected Class _char()
    {
        return primitive(Character());
    }
    
    protected Class _float()
    {
        return primitive(Float());
    }
    
    protected Class _short()
    {
        return primitive(Short());
    }
    
    protected Class _boolean()
    {
        return primitive(Boolean());
    }
    
    protected Class _long()
    {
        return primitive(Long());
    }        
    /***/
    
    
    /** Data set functions **/
    private void resetData() 
    {
       m_dataSets.clear();
       m_indexMap.clear();
       m_currentDataStructure = null;
       m_currentDataSet = null;
    }
    
    private boolean takeDataSet()
    {
        if (m_dataSets.isEmpty()) {
            m_currentDataSet = null;        
            return false;
        }
                                    
        m_currentDataSet = m_dataSets.removeFirst();
        return true;
    }
    
    public void defineDataStructure(Object ... args)
    {
        if (m_currentDataStructure != null || !m_dataSets.isEmpty() ||
            !m_indexMap.isEmpty())
            throw new RuntimeException("Cannot define data structure at this time.");
        
        if (args.length % 2 != 0)
            throw new RuntimeException("Invalid data definition");                            
            
        m_currentDataStructure = new Class[args.length / 2];
        
        int i=0;
        int currentIndex = 0;
        while (i < args.length) {
             Object o1 = args[i++];
             Object o2 = args[i++];
             
             if (!(o1 instanceof Class)) {             
                throw new RuntimeException("Invalid data definition. Type 'Class'" +
                                       " expected. " + o1.getClass().getName() +
                                       " found in parameter #" + i);
             }
                          
             if (!(o2 instanceof String)) {             
                throw new RuntimeException("Invalid data definition. Type 'String'" +
                                       " expected. " + o1.getClass().getName() +
                                       " found in parameter #" + i);
             } 
             
             if (m_indexMap.containsKey((String)o2)) {
                throw new RuntimeException("Variable " + o2 + " defined twice.");
             }
             
             
             m_currentDataStructure[currentIndex] = (Class)o1;
             m_indexMap.put((String)o2, currentIndex++);             
        }
    }
            
    public void addDataSet(String name, Object ... objs)
    {
        QVERIFY(m_currentDataStructure != null);
        QCOMPARE(objs.length, m_currentDataStructure.length);        
        
        for (int i=0; i<objs.length; ++i) {
            if (objs[i] != null) {            
                Class expectedClass = m_currentDataStructure[i];
                Class foundClass = objs[i].getClass();
                
                if (!expectedClass.isAssignableFrom(foundClass)) {
                    throw new RuntimeException("Invalid data set: '" + name + "', parameter #" + i + ". Expected subclass" +
                        " of '" + expectedClass.getName() + "' but found '" +
                        foundClass.getName() + "'.");
                }                        
            }
        }
     
        QTestData td = new QTestData();
        td.name = name;
        td.dataSet = objs;   
        m_dataSets.add(td);
    }
    
    public <T extends Object> T getParameter(String name)
    {
        if (m_currentDataSet == null)
            throw new RuntimeException("Can't get parameter at this time.");
            
        if (!m_indexMap.containsKey(name))
            throw new RuntimeException("No such parameter: " + name);
            
        int index = m_indexMap.get(name);               
        return (T) m_currentDataSet.dataSet[index];   
    }
    /***/
    
    
    private void runTestMethod(Method m)
    {
        Class clazz = getClass();
        
        String name = m.getName();
        name = name.replace("run_", "data_");
        
        resetData();                
        
        boolean done = false;
        Method dataMethod = null;         
        
        try {        
            dataMethod = clazz.getMethod(name);        
        } catch (NoSuchMethodException e) {
            dataMethod = null;
        }
        
        boolean passed = true;        
        if (dataMethod != null) {        
            try {            
                dataMethod.invoke(this);
            } catch (Throwable e) {
                
                if (e instanceof InvocationTargetException) {
                    InvocationTargetException ite = (InvocationTargetException)e;                    
                    e = ite.getTargetException();
                }
                
                // e.printStackTrace();
                
                QTestException e2;
                if (e instanceof QTestException) {
                    e2 = (QTestException) e;
                } else {                                                
                    StackTraceElement st = e.getStackTrace()[0];
                    e2 = new QTestException(st.getClassName(), 
                        st.getFileName(), st.getMethodName(), st.getLineNumber(),
                        e.getMessage());
                }                        
                    
                m_listExceptions.add(e2);
                    
                dataMethod = null;
                passed = false;
                done = true;
            }
        }
                
                
        takeDataSet();                                
        while (!done) {                       
            try {
                m.invoke(this);
             } catch (Throwable e) {                
                passed = false;
                
                if (e instanceof InvocationTargetException) {
                    InvocationTargetException ite = (InvocationTargetException)e;                    
                    e = ite.getTargetException();
                }                
                                
                
                // e.printStackTrace();                
                                
                
                QTestException te = null;
                if (e instanceof QTestException) {                                    
                
                    te = (QTestException) e;
                    m_listExceptions.add(te);
                } else {
                    StackTraceElement st = e.getStackTrace()[0];
                    te = new QTestException(st.getClassName(), 
                        st.getFileName(), st.getMethodName(), st.getLineNumber(),
                        e.toString());
                                                
                    m_listExceptions.add(te);
                }                 
                if (m_currentDataSet != null)
                    te.m_testDataName = m_currentDataSet.name;
                                
             }  
             if (dataMethod != null)
                done = !takeDataSet();
             else
                done = true;                                                          
       }
       
       if (passed) {       
            m_listExceptions.add(new QTestPassException(m.getName()));                
       }
    }
    
    private void displayResult()
    {
        for (int i=0; i<m_listExceptions.size(); ++i) {
            QTestException e = m_listExceptions.get(i);
            
            
            String message;
            if (e instanceof QTestPassException) {            
                message = "PASS: " + e.m_methodName;
            } else {            
                message = "FAIL: " + e.m_methodName;
                if (e.m_testDataName.length() > 0)
                    message = message + " [" + e.m_testDataName + "]";
                    
                message = message + 
                          "\n      " + e.m_fileName + "(" + e.m_lineNumber + ")";                          
                          
                message = message + 
                          "\n      " + e.m_message;                                                  
            }
            
            System.out.println(message);
        }
    }
    
    public static void runTest(QTestCase testCase)
    {
        Class clazz = testCase.getClass();
        
        Method methods[] = clazz.getMethods();
        
        for (int i=0; i<methods.length; ++i) {
            if (methods[i].getName().startsWith("run_")) {            
                testCase.runTestMethod(methods[i]);                
            }
        }
        
        
        testCase.displayResult();        
    }
    
    public static void main(String args[])
    {
        if (args.length == 0) {
            System.out.println("Please call test case with the name of the test case class as parameter or reimplement main()");
            return ;
        }
        
        QApplication app = new QApplication(args);
        
        String className = args[0];
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        
        Class clazz = null;
        try {        
            clazz = loader.loadClass(className);
        } catch (ClassNotFoundException e) {
            System.out.println("Class cannot be found: " + className);
            return ;
        }
        
        
        QTestCase tc = null;
        try {            
            tc = (QTestCase) clazz.newInstance();
        } catch (Exception e) {
            System.out.println("Cannot create instance of class: " + className);
	    // e.printStackTrace();
            return ;
        }
                        
        runTest(tc);                
    }
    
    protected void addSearchPath(String path, boolean recursive)
    {
        File f = new File(path);
        
        if (f.exists() && f.isDirectory()) {
            m_searchPaths.add(path);
            
            if (recursive) {
                File files[] = f.listFiles();
                
                for (int i=0; i<files.length; ++i) {                
                    String name = files[i].getName();
                    if (name != "." && name != "..")
                        addSearchPath(files[i].getPath(), true);
                }
            }
        }
    }
    
    private String extractLineOfText(File f, int lineNumber) 
    {        
        LineNumberReader reader;
        
        try {        
            reader = new LineNumberReader(new FileReader(f));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Cannot find file");
        }
        
        if (reader == null)
            return "";
                        
        String s = "";
        
        try {        
            for (int i=0; i<lineNumber; ++i)
                s = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException("Cannot read from file");
        }
        
        return s;
    }
    
    private String[] findFileText(String fileName, int lineNumber, 
                                String methodName)
    {
        QTestFilter filter = new QTestFilter(fileName);
        
        String text = null;
        for (int i=0; i<m_searchPaths.size(); ++i) {
            File f = new File(m_searchPaths.get(i));
            
            File files[] = f.listFiles(filter);
            for (int j=0; j<files.length; ++j) {
                if (files[j].isFile()) {
                    text = extractLineOfText(files[j], lineNumber);
                    break ;
                }
            }
        }
        
        if (text == null) {        
            String[] tmp = new String[1];
            tmp[0] = "Source file not found";
            return tmp;
        }                    
        int idx = text.indexOf(methodName);
        
        if (idx >= 0)
            text = text.substring(idx + methodName.length());
        
        idx = text.indexOf("(");        
        
        if (idx >= 0)
            text = text.substring(idx + 1);
        
        idx = text.lastIndexOf(")");
        if (idx >= 0)
            text = text.substring(0, idx).trim();
        
        StringTokenizer tokenizer = new StringTokenizer(text, ",", false);
        String returned[] = new String[tokenizer.countTokens()];
        for (int token=0; token<returned.length; ++token) 
            returned[token] = tokenizer.nextToken().trim();
            
        return returned;
    }
    
    public void QVERIFY(boolean condition)
    {
        QVERIFY(condition, null);
    }
    
    public void QVERIFY(boolean condition, String description) 
    {    
        if (condition)
            return ;
    
        StackTraceElement ste = null;
        StackTraceElement qtestFunctionCall = null;
        try {
            throw new Exception();
        } catch (Exception e) {                            
            StackTraceElement st[] = e.getStackTrace();
            
            for (int i=0; i<st.length; ++i) {
                if (st[i].getClassName() != "com.trolltech.qtest.QTestCase") {
                    if (i > 0) 
                        qtestFunctionCall = st[i - 1];
                    ste = st[i];
                    break ;
                }
            }            
        }
        
        if (ste == null || qtestFunctionCall == null)
            throw new RuntimeException("Can't find stack trace element");        
        
        String className = ste.getClassName();
        String fileName = ste.getFileName();
        String methodName = ste.getMethodName();
        int lineNumber = ste.getLineNumber();        
                
        String message = "Call to " + qtestFunctionCall.getMethodName() + " failed";
        if (description == null || description.length() == 0) {        
            message = message + " with parameters: ";
            String params[] = findFileText(fileName, lineNumber, 
                qtestFunctionCall.getMethodName());
                            
            for (int i=0; i<params.length; ++i) {
                if (i > 0)
                    message = message + ", ";
                message = message + params[i];
            }
        } else {
            message = message + ". Reason: " + description;
        }
        
        throw new QTestException(className, fileName, methodName, lineNumber,
            message);
    }    
    
    public void QVERIFY(long condition)
    {
        QVERIFY(condition != 0);
    }    

    
    public void QVERIFY(Object condition)
    {
        QVERIFY(condition != null);
    }
    
    public void QCOMPARE(Object ... list)
    {       
        if (list.length == 0)
           return ;
           
        Object test = list[0]; 
        for (int i=1; i<list.length; ++i) {
            try {            
                if (test == null || list[i] == null)
                    QVERIFY(test == list[i]);
                else
                    QVERIFY(test.equals(list[i]));    
            } catch (QTestException e) {
                if (e.m_message == null)
                    e.m_message = "";
                    
                if (test == null)
                    test = "null";
                if (list[i] == null)
                    list[i] = "null";
                    
                e.m_message = e.m_message + " (first item is " 
                    + test.toString() + " and " + (i+1) + "th item is " 
                    + list[i].toString() + ")";
                                                                        
                throw e;
            }
        }
    }
    
    public void QFAIL(String s)
    {
        QVERIFY(false, s);
    }
}



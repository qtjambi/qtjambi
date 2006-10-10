package com.trolltech.qt;

import java.lang.reflect.*;

public class QMalformedQtPropertyException extends Exception 
{
    public QMalformedQtPropertyException(String name, String message, Method method) {
        super(message);
        this.name = name;
        this.method = method;
    }
    
    public String name() { return name; }
    public Method method() { return method; }
    
    public String toString() { 
        return getMessage() + ", method: " + method;
    }
        
    private String name;
    private Method method;

    private static final long serialVersionUID = 1L;
}

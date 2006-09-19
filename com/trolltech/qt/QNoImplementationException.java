package com.trolltech.qt;

public class QNoImplementationException extends RuntimeException 
{
    private static final long serialVersionUID = 1L;
    private static final String NO_IMPLEMENTATION_STRING = 
        "The method has no implementation and only exists for compatibility";
    
    public QNoImplementationException() 
    {
        super(NO_IMPLEMENTATION_STRING);
    }

    public QNoImplementationException(Throwable throwable) 
    {
        super(NO_IMPLEMENTATION_STRING, throwable);
    } 
}

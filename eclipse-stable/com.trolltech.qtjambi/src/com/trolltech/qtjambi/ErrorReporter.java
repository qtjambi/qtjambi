package com.trolltech.qtjambi;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.util.ILogger;
import org.eclipse.jface.util.Policy;

public class ErrorReporter 
{   
    public static void reportError(Exception e, String message) 
    {
        ILogger logger = Policy.getLog();
        logger.log(new Status(
                     Status.WARNING,
                     "Qt Jambi Plugin",
                     Status.OK,
                     message,
                     e));
    }
}

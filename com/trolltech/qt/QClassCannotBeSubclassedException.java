package com.trolltech.qt;

public class QClassCannotBeSubclassedException extends Exception {

    private static final long serialVersionUID = 1L;

    public QClassCannotBeSubclassedException(Class<?> clazz) {
        super("Class '" + clazz.getName() + "' is not intended to be subclassed, and attempts to do so will lead to run time errors.");
    }
    
}

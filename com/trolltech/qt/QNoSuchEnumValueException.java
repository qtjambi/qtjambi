package com.trolltech.qt;

/**
 * 
 *
 */
public class QNoSuchEnumValueException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public QNoSuchEnumValueException(int value) {
        super(String.valueOf(value));
    }
    
}

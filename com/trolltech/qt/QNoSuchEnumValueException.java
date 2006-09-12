package com.trolltech.qt;

/**
 * 
 *
 */
public class QNoSuchEnumValueException extends RuntimeException {
    
    /**
     * @param arg0
     */
    public QNoSuchEnumValueException(int value) {
        super(String.valueOf(value));
    }
    
}

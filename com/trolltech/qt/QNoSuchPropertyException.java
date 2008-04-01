package com.trolltech.qt;

/**
 * This exception is deprecated. It is no longer thrown by any
 * methods due to a change in the property system.
 */
@Deprecated
public class QNoSuchPropertyException extends QPropertyException {
    private static final long serialVersionUID = 1L;

    public QNoSuchPropertyException(String message) {
        super(message);
    }
}

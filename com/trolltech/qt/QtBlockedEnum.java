package com.trolltech.qt;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The QtBlockedEnum annotation prevents an enum from being used 
 * as a type for properties in a QObject subclass (special rules
 * apply to the Qt interface.) It is provided for compatibility 
 * with the underlying Qt libraries.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface QtBlockedEnum {

}

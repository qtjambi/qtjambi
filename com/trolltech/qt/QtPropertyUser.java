package com.trolltech.qt;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * QtPropertyUser specifies that a proprety
 * is designated as the user-facing or user-editable property for the class. e.g., 
 * QAbstractButton.checked is the user editable property for (checkable) buttons. 
 * Note that QItemDelegate gets and sets a widget's USER property. 
 * This annotation should be used with the read method of the property.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface QtPropertyUser { }

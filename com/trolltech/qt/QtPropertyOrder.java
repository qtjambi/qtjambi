package com.trolltech.qt;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
public @interface QtPropertyOrder {
    public int value() default 0;
}

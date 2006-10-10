package com.trolltech.qt;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
public @interface QtPropertyReader {
    boolean enabled() default true; 
    String name() default "";
}

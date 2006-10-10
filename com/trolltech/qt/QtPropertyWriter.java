package com.trolltech.qt;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
public @interface QtPropertyWriter {
    boolean enabled() default true;
    String name() default "";
}

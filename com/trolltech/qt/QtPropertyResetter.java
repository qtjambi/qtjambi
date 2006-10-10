package com.trolltech.qt;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
public @interface QtPropertyResetter {
    boolean enabled() default false;
    String name() default "";
}

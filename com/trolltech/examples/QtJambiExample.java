package com.trolltech.examples;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
public @interface QtJambiExample {
    String canInstantiate() default "";
    String name() default "";
}

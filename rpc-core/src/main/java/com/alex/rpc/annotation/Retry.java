package com.alex.rpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Retry {
    Class<? extends Throwable> value() default Exception.class;

    int maxAttempts() default 3;

    int delay() default 0;
}

package com.alex.rpc.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Limit {
    /**
     * How many request is allowed per second
     */
    double permitsPerSecond();

    /**
     *  How long to wait if can not get a token
     */
    long timeout();
}

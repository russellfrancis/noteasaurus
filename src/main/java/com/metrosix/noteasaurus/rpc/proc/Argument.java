package com.metrosix.noteasaurus.rpc.proc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: Argument.java 247 2010-08-07 23:15:10Z adam $
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.FIELD})
public @interface Argument {
    boolean required();
    String name();
}

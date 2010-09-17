package com.metrosix.noteasaurus.rpc.proc;

import com.metrosix.noteasaurus.security.SecuredResource;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Russell Francis (russell.francis@gmail.com)
 * @veresion $Id: AssertSecurity.java 247 2010-08-07 23:15:10Z adam $
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface AssertSecurity {
   Class<? extends SecuredResource>[] canRead() default {};
   Class<? extends SecuredResource>[] canWrite() default {};
}

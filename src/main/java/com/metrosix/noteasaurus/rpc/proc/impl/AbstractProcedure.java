package com.metrosix.noteasaurus.rpc.proc.impl;

import com.metrosix.noteasaurus.rpc.proc.impl.exception.InvalidArgumentException;
import com.metrosix.noteasaurus.rpc.proc.impl.exception.NullArgumentException;
import com.metrosix.noteasaurus.rpc.proc.impl.exception.ArgumentException;
import com.metrosix.noteasaurus.rpc.proc.*;
import com.metrosix.noteasaurus.database.PersistenceManager;
import com.metrosix.noteasaurus.security.SecuredResource;
import com.metrosix.noteasaurus.security.SecurityPrincipal;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
abstract public class AbstractProcedure implements Procedure {
    private PersistenceManager persistenceManager;

    protected AbstractProcedure(PersistenceManager persistenceManager) {
        setPersistenceManager(persistenceManager);
    }

    @Override
    public boolean canExecute(SecurityPrincipal principal) {
        AssertSecurity assertSecurity = getClass().getAnnotation(AssertSecurity.class);
        if (assertSecurity == null) {
            return false;
        }

        Class<? extends SecuredResource>[] readResourceTypes = assertSecurity.canRead();
        for (Class<? extends SecuredResource> readResourceType : readResourceTypes) {
            if (!principal.canRead(readResourceType)) {
                return false;
            }
        }

        Class<? extends SecuredResource>[] writeResourceTypes = assertSecurity.canWrite();
        for (Class<? extends SecuredResource> writeResourceType : writeResourceTypes) {
            if (!principal.canWrite(writeResourceType)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void assignArguments(Map<String,Object> arguments)
    throws ArgumentException
    {
        try {
            this.assignArgumentsToFields(arguments);
            this.assignArgumentsToMethods(arguments);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (ArgumentException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to assign parameters to procedure: " + e.toString(), e);
        }
    }

    protected void assignArgumentsToMethods(Map<String,Object> parameters)
    throws IllegalAccessException, InvalidArgumentException, NullArgumentException,
    SecurityException, NoSuchMethodException, InvocationTargetException
    {
        Class currentClass = getClass();
        do {
            Method[] methods = currentClass.getDeclaredMethods();
            for (Method method : methods) {
                Argument parameter = method.getAnnotation(Argument.class);
                if (parameter != null) {
                    boolean required = parameter.required();
                    String name = parameter.name();

                    Object value = parameters.get(name);
                    if (required && value == null) {
                        throw new NullArgumentException(parameter);
                    }

                    if (value != null) {
                        assignMethod(parameter, method, value);
                    }
                }
            }
        } while( !(currentClass = currentClass.getSuperclass()).equals(Object.class));
    }

    protected void assignArgumentsToFields(Map<String,Object> parameters)
    throws ArgumentException, IllegalArgumentException, IllegalAccessException
    {
        Class currentClass = this.getClass();
        do {
            Field[] fields = currentClass.getDeclaredFields();
            for (Field field : fields) {
                Argument parameter = field.getAnnotation(Argument.class);
                if (parameter != null) {
                    boolean required = parameter.required();
                    String name = parameter.name();

                    Object value = parameters.get(name);
                    if (required && value == null) {
                        throw new NullArgumentException(parameter);
                    }

                    if (value != null) {
                        assignField(parameter, field, value);
                    }
                }
            }
        } while (!(currentClass = currentClass.getSuperclass()).equals(Object.class));
    }

    protected void assignField(Argument parameter, Field field, Object value)
    throws IllegalArgumentException, IllegalAccessException, InvalidArgumentException
    {
        if (parameter == null) {
            throw new IllegalArgumentException("The parameter parameter must be non-null.");
        }
        if (field == null) {
            throw new IllegalArgumentException("The parameter field must be non-null.");
        }
        if (value == null) {
            throw new IllegalArgumentException("The parameter value must be non-null.");
        }

        value = convertToAssignableType(parameter, field.getType(), value);
        field.setAccessible(true);
        field.set(this, value);
    }

    protected void assignMethod(Argument parameter, Method method, Object value)
    throws IllegalAccessException, InvalidArgumentException, SecurityException, NoSuchMethodException, InvocationTargetException
    {
        if (parameter == null) {
            throw new IllegalArgumentException("The parameter parameter must be non-null.");
        }
        if (method == null) {
            throw new IllegalArgumentException("The parameter method must be non-null.");
        }
        if (value == null) {
            throw new IllegalArgumentException("The parameter value must be non-null.");
        }

        Method mutator;

        if (!Void.TYPE.equals(method.getReturnType())) {
            // method is accessor.
            String mutatorName;
            if (method.getName().startsWith("is")) {
                mutatorName = "set" + method.getName().substring(2);
            } else {
                mutatorName = "set" + method.getName().substring(3);
            }
            mutator = method.getDeclaringClass().getMethod(mutatorName, method.getReturnType());
        }
        else {
            mutator = method;
        }

        value = convertToAssignableType(parameter, mutator.getParameterTypes()[0], value);
        mutator.setAccessible(true);
        mutator.invoke(this, value);
    }

    protected Object convertToAssignableType(Argument parameter, Class type, Object value)
    throws InvalidArgumentException
    {
        if (type == null) {
            throw new IllegalArgumentException("The parameter type must be non-null.");
        }
        if (value == null) {
            throw new IllegalArgumentException("The parameter type must be non-null.");
        }

        if (!type.isAssignableFrom(value.getClass())) {
            try {
                if (type.isAssignableFrom(Long.class)) {
                    value = Long.valueOf(value.toString());
                }
                else if (type.isAssignableFrom(Long.TYPE)) {
                    value = Long.parseLong(value.toString());
                }
                else if (type.isAssignableFrom(Integer.class)) {
                    value = Integer.valueOf(value.toString());
                }
                else if (type.isAssignableFrom(Integer.TYPE)) {
                    value = Integer.parseInt(value.toString());
                }
                else if (type.isAssignableFrom(Short.class)) {
                    value = Short.valueOf(value.toString());
                }
                else if (type.isAssignableFrom(Short.TYPE)) {
                    value = Short.parseShort(value.toString());
                }
                else if (type.isAssignableFrom(Boolean.class)) {
                    value = Boolean.valueOf(value.toString());
                }
                else if (type.isAssignableFrom(Boolean.TYPE)) {
                    value = Boolean.parseBoolean(value.toString());
                }
                else {
                    // We don't know how to assign it?
                    throw new InvalidArgumentException(parameter, type, value);
                }
            } catch (NumberFormatException e) {
                throw new InvalidArgumentException(parameter, type, value);
            }
        }

        return value;
    }

    protected PersistenceManager getPersistenceManager() {
        return persistenceManager;
    }

    protected void setPersistenceManager(PersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
    }
}

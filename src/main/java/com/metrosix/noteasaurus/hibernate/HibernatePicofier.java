package com.metrosix.noteasaurus.hibernate;

import com.metrosix.noteasaurus.domain.AbstractEntity;
import com.metrosix.noteasaurus.util.PicoContainerFactory;
import java.io.Serializable;
import java.util.Random;
import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.EntityMode;
import org.hibernate.type.Type;
import org.picocontainer.PicoContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
public class HibernatePicofier extends EmptyInterceptor {
    static private final Logger log = LoggerFactory.getLogger(HibernatePicofier.class);
    static private final Random random = new Random(System.currentTimeMillis());

    @Override
    public Object instantiate(String entityName, EntityMode entityMode, Serializable id) throws CallbackException {
        try {
            PicoContainer pico = PicoContainerFactory.getPicoContainer();
            Class entityClass = Class.forName(entityName);
            AbstractEntity newEntity = (AbstractEntity)pico.getComponent(entityClass);
            long lid = Long.parseLong(id.toString());
            newEntity.setId(lid);
            return newEntity;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unable to instantiate class '" + entityName + "': " + e.toString(), e);
        }
    }

    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        if (entity != null && entity instanceof AbstractEntity) {
            AbstractEntity persistent = (AbstractEntity)entity;
            if (persistent.getHashCode() == 0) {
                for (int i = 0; i < propertyNames.length; ++i) {
                    if ("hashCode".equals(propertyNames[i])) {
                        while ((state[i] = Integer.valueOf(random.nextInt())).equals(Integer.valueOf(0))) {
                        }
                        persistent.setHashCode((Integer)state[i]);
                        return true;
                    }
                }
            }
        }
        return false;
    }
}

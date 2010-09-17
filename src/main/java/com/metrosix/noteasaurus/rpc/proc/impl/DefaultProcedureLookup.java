package com.metrosix.noteasaurus.rpc.proc.impl;

import com.metrosix.noteasaurus.rpc.proc.Procedure;
import com.metrosix.noteasaurus.rpc.proc.ProcedureLookup;
import com.metrosix.noteasaurus.util.PicoContainerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import org.picocontainer.Characteristics;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Startable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
public class DefaultProcedureLookup implements ProcedureLookup, Startable {

    static private final Logger log = LoggerFactory.getLogger(DefaultProcedureLookup.class);

    private Map<String,Class<? extends Procedure>> procedureMap;

    @Override
    public void start() {
        try {
            Properties props = new Properties();
            InputStream ins = this.getClass().getResourceAsStream("/procedure.mapping.properties");
            try {
                props.load(ins);
            }
            finally {
                ins.close();
            }

            Map<String,Class<? extends Procedure>> procMap = new HashMap<String,Class<? extends Procedure>>();
            Set<Entry<Object,Object>> entrySet = props.entrySet();
            for (Entry<Object,Object> entry : entrySet) {
                String key = entry.getKey().toString();
                String value = entry.getValue().toString();
                try {
                    Class klass = Class.forName(value);
                    if (Procedure.class.isAssignableFrom(klass)) {
                        getPicoContainer().as(Characteristics.NO_CACHE).addComponent(klass);
                        procMap.put(key, klass);
                    }
                    else {
                        if (log.isErrorEnabled()) {
                            log.error("Unable to add procedure mapping for '" + key + "' to '" + value +
                                    "' the class does not implement procedure.");
                        }
                    }
                }
                catch (ClassNotFoundException e) {
                    if (log.isErrorEnabled()) {
                        log.error("Unable to create class instance of procedure '" + value + "'.: " + e.toString());
                    }
                }
            }

            setProcedureMap(procMap);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        setProcedureMap(null);
    }

    @Override
    public Procedure lookup(String procedureName) {
        Class<? extends Procedure> klass = getProcedureMap().get(procedureName);
        if (klass != null) {
            return (Procedure)getPicoContainer().getComponent(klass);
        }
        return null;
    }

    protected void setProcedureMap(Map<String, Class<? extends Procedure>> procedureMap) {
        this.procedureMap = procedureMap != null ? Collections.unmodifiableMap(procedureMap) : null;
    }

    protected Map<String,Class<? extends Procedure>> getProcedureMap() {
        return procedureMap;
    }

    protected MutablePicoContainer getPicoContainer() {
        return PicoContainerFactory.getPicoContainer();
    }
}

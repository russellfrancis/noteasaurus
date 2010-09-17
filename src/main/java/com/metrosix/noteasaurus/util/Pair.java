package com.metrosix.noteasaurus.util;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
public class Pair<K,V> {
    private K key;
    private V value;

    public Pair(K key, V value) {
        setKey(key);
        setValue(value);
    }

    public K getKey() {
        return key;
    }

    protected void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    protected void setValue(V value) {
        this.value = value;
    }
}

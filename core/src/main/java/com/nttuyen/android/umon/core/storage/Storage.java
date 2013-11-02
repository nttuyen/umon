package com.nttuyen.android.umon.core.storage;

import java.util.Map;

/**
 * @author nttuyen266@gmail.com
 */
public interface Storage {
	public <K, V> void put(K key, V value);
	public <K, V> V get(K key);
	public <K, V> Map<K, V> map();
}

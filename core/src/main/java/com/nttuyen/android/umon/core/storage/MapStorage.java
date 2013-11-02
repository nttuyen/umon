package com.nttuyen.android.umon.core.storage;

import java.util.HashMap;
import java.util.Map;

/**
 * @author nttuyen266@gmail.com
 */
public class MapStorage implements Storage {
	protected Map maps = new HashMap();

	@Override
	public <K, V> void put(K key, V value) {
		maps.put(key, value);
	}

	@Override
	public <K, V> V get(K key) {
		try {
			if(maps.containsKey(key)) {
				return (V)maps.get(key);
			}
		} catch (Throwable exception) {}

		return null;
	}

	@Override
	public <K, V> Map<K, V> map() {
		return maps;
	}
}

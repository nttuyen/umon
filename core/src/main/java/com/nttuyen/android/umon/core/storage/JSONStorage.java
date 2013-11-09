package com.nttuyen.android.umon.core.storage;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Iterator;

/**
 * @author nttuyen266@gmail.com
 */
public abstract class JSONStorage implements Storage {
	private static final String TAG = "[umon][core] " + JSONStorage.class.getName();
	protected JSONObject data = new JSONObject();

	@Override
	public <K, V> void put(K key, V value) {
		if(key == null || value == null) {
			Log.e(TAG, "Key or value is null, so value is not storage");
			return;
		}
		String name = key instanceof String ? (String)key : key.toString();
		try {
			data.put(name, value);
		} catch (JSONException ex) {
			Log.e(TAG, "JSONException", ex);
		}
	}

	@Override
	public <K, V> V get(K key) {
		if(key == null) {
			Log.e(TAG, "Key, NULL will be return");
			return null;
		}

		String name = key instanceof String ? (String)key : key.toString();
		try {
			return (V)data.get(name);
		} catch (JSONException ex) {
			Log.e(TAG, "JSONException", ex);
		} catch (ClassCastException ex) {
			Log.e(TAG, "class cast exception", ex);
		}

		return null;
	}

	@Override
	public <K, V> V remove(K key) {
		if(key == null) {
			Log.e(TAG, "Key is null, so nothing will be removed");
			return null;
		}
		try {
			String name = key instanceof String ? (String)key : key.toString();
			if(!data.isNull(name)) {
				Object value = data.get(name);
				data.remove(name);

				return (V)value;
			}
		} catch (JSONException ex) {
			Log.e(TAG, "JSon exception", ex);
		} catch (ClassCastException ex) {
			Log.e(TAG, "Class cast exception", ex);
		}

		return null;
	}

	@Override
	public <K> K[] keys() {
		try {
			int size = data.length();
			K[] keys = (K[])new Object[size];
			Iterator<String> iterator = data.keys();
			int i = 0;
			while (iterator.hasNext()) {
				keys[i++] = (K)iterator.next();
			}

			return keys;
		} catch (ClassCastException ex) {
			Log.e(TAG, "Class cast exception", ex);
		}

		return null;
	}

	/**
	 * Write data to persist storage
	 */
	public abstract void persist();

	/**
	 * Read data from persist storage
	 */
	public abstract void fetch();
}

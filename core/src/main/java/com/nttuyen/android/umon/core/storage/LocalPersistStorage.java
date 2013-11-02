package com.nttuyen.android.umon.core.storage;

import android.util.Log;

import java.util.Collections;
import java.util.Map;

/**
 * @author nttuyen266@gmail.com
 */
public abstract class LocalPersistStorage extends MapStorage {
	private static final String TAG = "[umon][core]" + LocalPersistStorage.class.getName();

	public static final int STATE_NOT_INIT = 0;
	public static final int STATE_LOADING = 1;
	public static final int STATE_LOAD_FAILURE = 2;
	public static final int STATE_DATA_OK = 3;
	public static final int STATE_PERSISTING = 4;
	public static final int STATE_PERSIST_FAILURE = 5;
	public static final int STATE_PERSISTED = 6;

	protected int state = STATE_NOT_INIT;

	/**
	 * After put ok, if state is persisted, we should change to state_data_ok
	 * @param key
	 * @param value
	 * @param <K>
	 * @param <V>
	 */
	@Override
	public <K, V> void put(K key, V value) {
		if(state < STATE_DATA_OK) {
			Log.w(TAG, "State is not init or loading or load data failure, so maybe you will not put success");
		} else if(state == STATE_PERSISTING) {
			Log.w(TAG, "State is persisting, so maybe you will not put data success");
		}

		super.put(key, value);

		if(state == STATE_PERSISTED) {
			state = STATE_DATA_OK;
		}
	}

	@Override
	public <K, V> V get(K key) {
		if(state < STATE_DATA_OK) {
			Log.w(TAG, "State is not init or loading or load data failure, so you will get null data");
		}

		return super.get(key);
	}

	@Override
	public <K, V> Map<K, V> map() {
		if(state < STATE_DATA_OK) {
			Log.w(TAG, "State is not init or loading or data loading failure, so you maybe get an empty map");
		}
		return super.map();
	}

	/**
	 * Set STATE = STATE_LOADING before start
	 * set STATE = STATE_LOAD_FAILURE if any error occur
	 * set STATE = STATE_DATA_OK if load successfully
	 */
	public abstract void persistToLocal();

	/**
	 * set STATE = STATE_PERSISTING before start
	 * set STATE = STATE_PERSIST_FAILURE if any error occur
	 * set STATE = STATE_PERSISTED when persist successfully
	 */
	public abstract void loadFromLocal();

	public int getState() {
		return this.state;
	}
}

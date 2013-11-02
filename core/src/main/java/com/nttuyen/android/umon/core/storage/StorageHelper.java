package com.nttuyen.android.umon.core.storage;

import android.os.AsyncTask;
import android.util.Log;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class sure that one install of each storage
 * @author nttuyen266@gmail.com
 */
public class StorageHelper {
	private static final String TAG = "[umon][core] "+StorageHelper.class.getName();

	private static Map<String, Storage> instances = new ConcurrentHashMap<String, Storage>();

	public static void registerStorage(String name, final Storage storage) {
		if(name == null || storage == null) {
			Log.e(TAG, "StorageName and Storage must not be null");
		}
		instances.put(name, storage);

		if(storage instanceof LocalPersistStorage) {
			AsyncTask task = new AsyncTask() {
				@Override
				protected Object doInBackground(Object... objects) {
					((LocalPersistStorage) storage).loadFromLocal();
					return null;
				}
			};
			task.execute(null);

			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				@Override
				public void run() {
					((LocalPersistStorage) storage).persistToLocal();
				}
			}));
		}
	}

	public static Storage getStorage(String name) {
		if(name == null) {
			throw new IllegalArgumentException("StorageName must not be null");
		}

		if(instances.containsKey(name)) {
			return instances.get(name);
		}

		try {
			Class<? extends Storage> type = (Class<? extends Storage>)Class.forName(name);
			final Storage storage = type.newInstance();
			instances.put(name, storage);

			if(storage instanceof LocalPersistStorage) {
				AsyncTask task = new AsyncTask() {
					@Override
					protected Object doInBackground(Object... objects) {
						((LocalPersistStorage) storage).loadFromLocal();
						return null;
					}
				};
				task.execute(null);

				Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
					@Override
					public void run() {
						((LocalPersistStorage) storage).persistToLocal();
					}
				}));
			}
		} catch (Throwable ex) {}

		return null;
	}
	public static Storage getStorage(Class type) {
		if(type == null) {
			throw new IllegalArgumentException("Type must not be null");
		}
		return getStorage(type.getName());
	}
}

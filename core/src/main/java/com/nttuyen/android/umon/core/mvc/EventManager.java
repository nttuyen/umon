package com.nttuyen.android.umon.core.mvc;

import com.nttuyen.android.umon.core.Callback;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Event manager
 * @author nttuyen266@gmail.com
 */
class EventManager {
	private final Map<String, Set<Callback>> handlers = new HashMap<String, Set<Callback>>();

	void on(String event, Callback callback) {
		Set<Callback> set = null;
		if(!handlers.containsKey(event)) {
			set = new HashSet<Callback>();
		} else {
			set = this.handlers.get(event);
		}

		set.add(callback);
		handlers.put(event, set);
	}
	void off(String event) {
		if(handlers.containsKey(event)) {
			return;
		}
		handlers.remove(event);
	}
	void off() {
		this.handlers.clear();
	}

	void trigger(String eventName, Object... params) {
		if(!handlers.containsKey(eventName)) {
			return;
		}
		Set<Callback> set = handlers.get(eventName);
		for(Callback callback : set) {
			callback.execute(params);
		}
	}
}

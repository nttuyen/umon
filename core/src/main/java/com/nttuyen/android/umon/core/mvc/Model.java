package com.nttuyen.android.umon.core.mvc;

import com.nttuyen.android.umon.core.Callback;

import java.util.*;

/**
 * @author nttuyen266@gmail.com
 */
public abstract class Model {
	/**
	 * Param for this event:
	 * params[0] = Model raise event
	 * params[1] = Process name
	 */
	public static final String ON_PROCESS_START = "eventOnProcessStart";
	/**
	 * Param for this event
	 * 1 => Model raise event
	 * 2 => Process name
	 * 3 => progress value
	 */
	public static final String ON_PROCESS_PROGRESS = "eventOnProcessProgress";
	/**
	 * Param for this event
	 * 1 => Model raise event
	 * 2 => Process name
	 */
	public static final String ON_PROCESS_COMPLETED = "eventOnProcessCompleted";
	/**
	 * Param for this event:
	 * 1 => Model raise event
	 * 2 => Process name
	 * 3 => error type
	 * 4 => error message
	 * 5 => exception
	 */
	public static final String ON_PROCESS_ERROR = "eventOnProcessError";
	/**
	 * Param for this event
	 * 1 => Model raise event
	 * 2 => fieldChange: separate by ','
	 */
	public static final String ON_CHANGE = "eventOnChange";

	private Map<String, Set<Callback>> handlers = new HashMap<String, Set<Callback>>();

	void on(String event, Callback callback) {
		Set<Callback> set = null;
		if(!handlers.containsKey(event)) {
			set = new LinkedHashSet<Callback>();
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

	protected void trigger(String eventName, Object... params) {
		if(!handlers.containsKey(eventName)) {
			return;
		}
		Set<Callback> set = handlers.get(eventName);
		for(Callback callback : set) {
			callback.execute(params);
		}
	}

	public abstract void fetch();
	public abstract void save();

	public static abstract class Collection<T extends Model> extends Model {
		/**
		 * Param for this event:
		 * 1 => Model raise event
		 * 2 => Child object
		 */
		public static final String ON_ADD = "onAdd";
		/**
		 * Param for this event
		 * 1 => Model raise event
		 * 2 => Child object
		 */
		public static final String ON_REMOVE = "onRemove";

		protected java.util.Collection<T> children = new LinkedList<T>();

		public void add(T child) {
			this.children.add(child);
			trigger(ON_ADD, this, child);
			trigger(ON_CHANGE, this, "");
		}
		public void remove(T child) {
			if(this.children.remove(child)) {
				trigger(ON_REMOVE, this, child);
				trigger(ON_CHANGE, this, "");
			}
		}
	}
}

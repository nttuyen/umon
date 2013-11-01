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
	 * if one file change, it will be: ON_CHANGE:FIELD_NAME or ON_CHANGE[INDEX_IN_COLLECTION]
	 */
	public static final String ON_CHANGE = "eventOnChange";

	private Map<String, Set<Callback>> handlers = new HashMap<String, Set<Callback>>();

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

	protected void trigger(String eventName, Object... params) {
		if(!handlers.containsKey(eventName)) {
			return;
		}
		Set<Callback> set = handlers.get(eventName);
		for(Callback callback : set) {
			callback.execute(params);
		}
	}

	public abstract <K> K getId();
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

		protected List<T> children = new LinkedList<T>();

		public void add(T child) {
			this.children.add(child);
			trigger(ON_ADD, this, child);
		}
		public void remove(T child) {
			if(this.children.remove(child)) {
				trigger(ON_REMOVE, this, child);
			}
		}

		public int indexOf(T child) {
			return children.indexOf(child);
		}

		public T get(int index) {
			return children.get(index);
		}

		public void set(int index, T child) {
			this.children.set(index, child);
			trigger(ON_CHANGE+"["+ index +"]", this);
		}
		public int size() {
			return children.size();
		}
	}
}

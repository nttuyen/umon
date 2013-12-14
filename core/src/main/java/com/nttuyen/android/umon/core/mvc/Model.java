package com.nttuyen.android.umon.core.mvc;

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

	private final EventManager eventManager = new EventManager();
	EventManager getEventManager() {
		return this.eventManager;
	}
	protected void trigger(String name, Object... params) {
		eventManager.trigger(name, params);
	}

	public abstract void fetch();
	public abstract void save();
}

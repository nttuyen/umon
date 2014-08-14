package com.nttuyen.android.umon.core.mvc;

/**
 * @author nttuyen266@gmail.com
 */
public abstract class Model {
    public static final String EVENT_ALL = "ALL";
    public static final String EVENT_ASYNC_START = "onAsyncProcessStart";
    public static final String EVENT_ASYNC_COMPLETED = "onAsyncProcessCompleted";
    public static final String EVENT_ASYNC_FAILED = "onAsyncProcessFailed";
    public static final String EVENT_ASYNC_SUCCESS = "onAsyncProcessSuccess";
    public static final String EVENT_CHANGE = "onChange";

	private final EventManager eventManager = new EventManager();
	EventManager getEventManager() {
		return this.eventManager;
	}

	protected void trigger(String name, Object... params) {
		eventManager.trigger(name, params);
	}
}

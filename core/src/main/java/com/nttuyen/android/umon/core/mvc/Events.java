package com.nttuyen.android.umon.core.mvc;

import com.nttuyen.android.umon.core.Callback;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * @author nttuyen266@gmail.com
 */
public class Events {
	public static void on(Model model, String event, Callback callback) {
		if(model != null && callback != null) {
			model.on(event, callback);
		}
	}

	public static void on(Model model, String event, String callback, Object context) {
		if(model == null || callback == null || context == null || event == null) {
			return;
		}

		on(model, event, Callback.MethodCallback.newInstance(callback, context));
	}

	public static void on(Model model, String event, Method method, Object context) {
		if(model != null && method != null && context != null) {
			on(model, event, new Callback.MethodCallback(method, context));
		}
	}

	public static void off(Model model, String event) {
		if(model != null) {
			model.off(event);
		}
	}
	public static void off(Model model) {
		if(model != null) {
			model.off();
		}
	}

	/**
	 * Register all event from target using annotation
	 * @param model - The model
	 * @param target - The object that has method listen on Model EVENT (using annotation)
	 */
	public static void registerAllEvents(Model model, Object target) {
		if(model == null || target == null) {
			return;
		}
		//Clean all old eventListener
		Events.off(model);

		Class type = target.getClass();

		//All method declared at current class is high priority
		Method[] methods = type.getDeclaredMethods();
		Set<String> registered = new HashSet<String>();
		for(Method method : methods) {
			EventListener eventListener = method.getAnnotation(EventListener.class);
			if(eventListener != null && eventListener.event() != null && !"".equals(eventListener.event())) {
				String event = eventListener.event();
				Events.on(model, event, method, target);
				registered.add(event);
			}
		}

		//All public method should be load but do not override
		methods = type.getMethods();
		for(Method method : methods) {
			EventListener eventListener = method.getAnnotation(EventListener.class);
			if(eventListener != null && eventListener.event() != null && !"".equals(eventListener.event())) {
				String event = eventListener.event();
				if(!registered.contains(event)) {
					Events.on(model, event, method, target);
				}
			}
		}
	}
}

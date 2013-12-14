package com.nttuyen.android.umon.core.mvc;

import com.nttuyen.android.umon.core.Callback;
import com.nttuyen.android.umon.core.MethodCallback;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * @author nttuyen266@gmail.com
 */
public class ModelEvents {
	public static void on(Model model, String event, Callback callback) {
		if(model != null && callback != null) {
			model.on(event, callback);
		}
	}

	public static void on(Model model, String event, String callback, Object context) {
		if(model == null || callback == null || context == null || event == null) {
			return;
		}

		on(model, event, MethodCallback.newInstance(callback, context));
	}

	public static void on(Model model, String event, Method method, Object context) {
		if(model != null && method != null && context != null) {
			on(model, event, new MethodCallback(method, context));
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

		Class type = target.getClass();

		//All method declared at current class is high priority
		Method[] methods = type.getDeclaredMethods();
		Set<String> registered = new HashSet<String>();
		for(Method method : methods) {
			ModelEventListener modelEventListener = method.getAnnotation(ModelEventListener.class);
			if(modelEventListener != null && modelEventListener.events() != null && modelEventListener.events().length > 0) {
				String[] events = modelEventListener.events();
				for(String event : events) {
					ModelEvents.on(model, event, method, target);
					registered.add(event);
				}
			}
		}

		//All public method should be load but do not override
		methods = type.getMethods();
		for(Method method : methods) {
			ModelEventListener modelEventListener = method.getAnnotation(ModelEventListener.class);
			if(modelEventListener != null && modelEventListener.events() != null && modelEventListener.events().length > 0) {
				String[] events = modelEventListener.events();
				for(String event : events) {
					if(!registered.contains(event)) {
						ModelEvents.on(model, event, method, target);
					}
				}
			}
		}
	}
}

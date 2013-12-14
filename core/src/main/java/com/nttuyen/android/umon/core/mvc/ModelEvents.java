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
			model.getEventManager().on(event, callback);
		}
	}
	/**
	 * Register all event from target using annotation
	 * @param model - The model
	 * @param target - The object that has method listen on Model EVENT (using annotation)
	 */
	public static void on(Model model, Object target) {
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
					ModelEvents.on(model, event, new MethodCallback(method, target));
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
						ModelEvents.on(model, event, new MethodCallback(method, target));
					}
				}
			}
		}
	}
	public static void off(Model model, String event) {
		if(model != null) {
			model.getEventManager().off(event);
		}
	}
	public static void off(Model model) {
		if(model != null) {
			model.getEventManager().off();
		}
	}


	public static void on(Collection collection, String event, Callback callback) {
		if(collection != null && callback != null) {
			collection.getEventManager().on(event, callback);
		}
	}
	/**
	 * Register all event from target using annotation
	 * @param collection - The model
	 * @param target - The object that has method listen on Model EVENT (using annotation)
	 */
	public static void on(Collection collection, Object target) {
		if(collection == null || target == null) {
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
					ModelEvents.on(collection, event, new MethodCallback(method, target));
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
						ModelEvents.on(collection, event, new MethodCallback(method, target));
					}
				}
			}
		}
	}
	public static void off(Collection collection, String event) {
		if(collection != null) {
			collection.getEventManager().off(event);
		}
	}
	public static void off(Collection collection) {
		if(collection != null) {
			collection.getEventManager().off();
		}
	}
}

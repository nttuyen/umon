package com.nttuyen.android.umon.core;

import android.util.Log;

import java.lang.reflect.Method;

/**
 * @author nttuyen266@gmail.com
 */
public class MethodCallback implements Callback {
	private static final String TAG = "[umon][core] MethodCallback";
	private final Method method;
	private final Object context;

	public MethodCallback(Method method, Object context) {
		this.method = method;
		this.context = context;
	}
	@Override
	public void execute(Object... params) {
		if(method == null || context == null) {
			return;
		}
		try {
			Method m = this.method;
			if(!m.isAccessible()) {
				m.setAccessible(true);
			}
			Class[] paramTypes = m.getParameterTypes();
			//In some case we don't need param call back
			if(paramTypes.length == 0) {
				m.invoke(this.context);
				return;
			}
			//Re-init params
			Object[] newParams = new Object[paramTypes.length];

			if(paramTypes.length < params.length) {
				System.arraycopy(params, 0, newParams, 0, paramTypes.length);
			} else {
				System.arraycopy(params, 0, newParams, 0, params.length);
			}
			for(int i = 0; i < newParams.length; i++) {
				if(newParams[i] != null) {
					try {
						if(paramTypes[i].isPrimitive()) {
							//TODO: do nothing here
						} else {
							newParams[i] = paramTypes[i].cast(newParams[i]);
						}
					} catch (Exception e) {
						newParams[i] = null;
					}
				}
			}
			m.invoke(this.context, newParams);
		} catch (Exception ex) {
			Log.e(TAG, "Exception", ex);
		}
	}

	@Override
	public int hashCode() {
		if(this.method == null || this.context == null) {
			return 0;
		}
		return (this.method.toString() + this.context.toString()).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == this) return true;

		if(!(obj instanceof MethodCallback)) {
			return false;
		}
		MethodCallback value = (MethodCallback)obj;

		return this.context.equals(value.context) && this.method.equals(value.method);
	}

	public static MethodCallback newInstance(String methodName, Object context) {
		if(methodName == null || context == null || "".equals(methodName)) {
			return null;
		}
		Class contextType = context.getClass();

		//All method declared at current class is high priority
		Method[] methods = contextType.getDeclaredMethods();
		int occur = 0;
		Method method = null;
		for(Method m : methods) {
			if(m.getName().equals(methodName)) {
				occur ++;
				method = m;
			}
		}

		//Error on too many method with same name
		if(occur > 1) {
			throw new IllegalArgumentException("Too many method with name: " + contextType.getClass().getName() + "#" + methodName);
		}

		//If found method in current class: return
		if(method != null) {
			return new MethodCallback(method, context);
		}



		//Continue find method in parent class
		methods = contextType.getMethods();
		occur = 0;
		method = null;
		for(Method m : methods) {
			if(m.getName().equals(methodName)) {
				occur ++;
				method = m;
			}
		}

		//Error on too many method with same name
		if(occur > 1) {
			throw new IllegalArgumentException("Too many method with name: " + contextType.getClass().getName() + "#" + methodName);
		}

		if(method == null || occur < 1) {
			//throw new IllegalArgumentException("Can not find method: "+ contextType.getClass().getName() + "#"  + methodName);
			return null;
		}

		return new MethodCallback(method, context);
	}
}

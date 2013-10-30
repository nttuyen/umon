package com.nttuyen.android.umon.core;

import java.lang.reflect.Method;

/**
 * @author nttuyen266@gmail.com
 */
public interface Callback {
	public void execute(Object... params);

	/**
	 * A reflection method callback
	 */
	static final class MethodCallback implements Callback {
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
							newParams[i] = paramTypes[i].cast(newParams[i]);
						} catch (Exception e) {
							newParams[i] = null;
						}
					}
				}
				m.invoke(this.context, newParams);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		public static MethodCallback newInstance(String methodName, Object context) {
			if(methodName == null || context == null || "".equals(methodName)) {
				return null;
			}

			//Find method in context
			Class contextType = context.getClass();
			Method[] methods = contextType.getMethods();

			int occur = 0;
			Method method = null;
			for(Method m : methods) {
				if(m.getName().equals(methodName)) {
					occur ++;
					method = m;
				}
			}
			if(method == null || occur < 1) {
				throw new IllegalArgumentException("Can not find method: "+ contextType.getClass().getName() + "#"  + methodName);
			}
			if(occur > 1) {
				throw new IllegalArgumentException("Too many method with name: " + contextType.getClass().getName() + "#" + methodName);
			}

			return new MethodCallback(method, context);
		}
	}
}

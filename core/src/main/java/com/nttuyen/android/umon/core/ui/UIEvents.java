package com.nttuyen.android.umon.core.ui;

import android.app.Activity;
import android.view.View;
import com.nttuyen.android.umon.core.Callback;
import com.nttuyen.android.umon.core.MethodCallback;
import com.nttuyen.android.umon.core.mvc.ModelEventListener;
import java.lang.reflect.Method;


/**
 * @author nttuyen266@gmail.com
 */
public class UIEvents {
	public static void onClick(View view, final Callback callback) {
		if(view == null || callback == null) {
			return;
		}
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				callback.execute(view);
			}
		});
	}

	public static void on(View root, Object target) {
		if(root == null || target == null) {
			return;
		}
		Class type = target.getClass();

		//All method declared at current class is high priority
		Method[] methods = type.getDeclaredMethods();
		for(Method method : methods) {
			UIOnclick uiOnclick = method.getAnnotation(UIOnclick.class);
			if(uiOnclick != null) {
				int[] viewIds = uiOnclick.views();
				for(int viewId : viewIds) {
					View view = root.findViewById(viewId);
					if(view != null) {
						Callback methodCallback = new MethodCallback(method, target);
						onClick(view, methodCallback);
					}
				}
			}
		}

		//All public method should be load but do not override
		methods = type.getMethods();
		for(Method method : methods) {
			UIOnclick uiOnclick = method.getAnnotation(UIOnclick.class);
			if(uiOnclick != null) {
				int[] viewIds = uiOnclick.views();
				for(int viewId : viewIds) {
					View view = root.findViewById(viewId);
					if(view != null) {
						Callback methodCallback = new MethodCallback(method, target);
						onClick(view, methodCallback);
					}
				}
			}
		}
	}

	public static void on(Activity activity, Object target) {
		if(activity == null || target == null) {
			return;
		}
		Class type = target.getClass();

		//All public method should be load but do not override
		Method[] methods = type.getMethods();
		for(Method method : methods) {
			UIOnclick uiOnclick = method.getAnnotation(UIOnclick.class);
			if(uiOnclick != null) {
				int[] viewIds = uiOnclick.views();
				for(int viewId : viewIds) {
					View view = activity.findViewById(viewId);
					if(view != null) {
						Callback methodCallback = new MethodCallback(method, target);
						onClick(view, methodCallback);
					}
				}
			}
		}

		//All method declared at current class is high priority
		methods = type.getDeclaredMethods();
		for(Method method : methods) {
			UIOnclick uiOnclick = method.getAnnotation(UIOnclick.class);
			if(uiOnclick != null) {
				int[] viewIds = uiOnclick.views();
				for(int viewId : viewIds) {
					View view = activity.findViewById(viewId);
					if(view != null) {
						Callback methodCallback = new MethodCallback(method, target);
						onClick(view, methodCallback);
					}
				}
			}
		}
	}
}

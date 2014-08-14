package com.nttuyen.android.umon.core.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
import com.nttuyen.android.umon.core.Callback;
import com.nttuyen.android.umon.core.async.Async;
import com.nttuyen.android.umon.core.mvc.Collection;
import com.nttuyen.android.umon.core.mvc.ModelEvents;
import com.nttuyen.android.umon.core.mvc.Model;
import com.nttuyen.android.umon.core.mvc.ModelEventListener;
import com.nttuyen.android.umon.core.reflect.ReflectUtil;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author nttuyen266@gmail.com
 */
public class UmonView extends RelativeLayout {
	private static final String TAG = "[umon][core] " + UmonView.class.getName();

	private LayoutInflater inflater;

	protected Model model = null;
	protected final State state = new State(this);

	public UmonView(Context context) {
		super(context);
		this.init(context, null, 0);
	}
	public UmonView(Context context, boolean isViewInList) {
		super(context);
		state.isViewInList = isViewInList;
		this.init(context, null, 0);
	}
	public UmonView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.init(context, attrs, 0);
	}
	public UmonView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.init(context, attrs, defStyle);
	}
	protected void init(Context context, AttributeSet attrs, int defStyle) {
		this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		//TODO: init useDialogForLoading, isViewInList, loadingLayoutId

		//Init loading view
		setLoadingLayout(0);

		//Init rootView if exists
		this.setLayout(0);
	}

	public void setLoadingLayout(final int layoutId) {
		if((state.loadingView != null) && (layoutId == 0 || layoutId == state.loadingLayoutId)) {
			return;
		}
		state.loadingLayoutId = layoutId;

		final View oldLoadingView = state.loadingView;
		state.loadingView = layoutId > 0 ? inflater.inflate(layoutId, null) : new ProgressBar(this.getContext());

		if(state.useDialogForLoading) {
			if(state.dialog == null) {
				state.dialog = new Dialog(this.getContext());
				state.dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				state.dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
				state.dialog.setCancelable(false);
			}
			state.dialog.setContentView(state.loadingView);
		} else {
			((Activity)this.getContext()).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(oldLoadingView != null) {
						UmonView.this.removeView(oldLoadingView);
					}

					RelativeLayout.LayoutParams params;
					if(state.loadingView instanceof ProgressBar) {
						params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
						params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
					} else {
						params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
					}
					UmonView.this.addView(state.loadingView, params);
					state.loadingView.setVisibility(oldLoadingView != null ? oldLoadingView.getVisibility() : View.VISIBLE);
				}
			});
		}
	}
	public void setLayout(final int layoutId) {
		if(layoutId == 0 || layoutId == state.layoutId) {
			return;
		}

		state.layoutId = layoutId;

		//Backup oldRootView
		final View oldRootView = state.rootView;
		state.rootView = inflater.inflate(layoutId, null);

		//Add rootView to this
		((Activity)this.getContext()).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(oldRootView != null) {
					UmonView.this.removeView(oldRootView);
				}
				UmonView.this.addView(state.rootView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
				state.rootView.setVisibility(oldRootView != null ? oldRootView.getVisibility() : View.GONE);
			}
		});

		//Init Event handler
		//TODO: should move this to background?
		state.mapViews.clear();
		state.initMapViews(UmonView.this.model);

		Context context = UmonView.this.getContext();
		if(context instanceof Activity) {
			UIEvents.on(state.rootView, context);
		}
	}

	public void setModel(final Model model) {
		if(model == null || model == this.model) return;
		this.model = model;

		//TODO: how to process this on background
		try {
			ModelEvents.off(model);
			ModelEvents.on(model, UmonView.this);

			//Init mapViews
			if(model.getClass() != state.modelClass) {
				UILayout uiLayout = model.getClass().getAnnotation(UILayout.class);
				if(uiLayout != null) {
					setLayout(state.isViewInList ? uiLayout.layoutInListView() : uiLayout.layout());
				}
				state.mapViews.clear();
				state.initMapViews(UmonView.this.model);
			}
		} catch (Throwable ex) {
			Log.e(TAG, "Exception when set model", ex);
		}
	}

	@ModelEventListener(events = {Model.ON_PROCESS_START})
	public void showLoading() {
		if(state.useDialogForLoading) {
			if(state.dialog != null && !state.dialog.isShowing()) {
				state.dialog.show();
			}
		} else {
			((Activity)this.getContext()).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(state.rootView != null && state.rootView.getVisibility() != View.GONE) {
						state.rootView.setVisibility(View.GONE);
					}
					if(state.loadingView != null && state.loadingView.getVisibility() != View.VISIBLE) {
						state.loadingView.setVisibility(View.VISIBLE);
					}
				}
			});
		}
	}
	protected void dismissLoading() {
		((Activity)this.getContext()).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(state.useDialogForLoading) {
					if(state.dialog != null && state.dialog.isShowing()) {
						state.dialog.dismiss();
					}
				} else {
					if(state.loadingView != null && state.loadingView.getVisibility() != View.GONE) {
						state.loadingView.setVisibility(View.GONE);
					}
				}
			}
		});
	}

	@ModelEventListener(events = {Model.ON_PROCESS_COMPLETED, Model.ON_PROCESS_ERROR})
	public void renderContent() {
		Async.execute(
				null,
				new Callback() {
					@Override
					public void execute(Object... params) {
						state.render(UmonView.this.model);
					}
				},
				new Callback() {
					@Override
					public void execute(Object... params) {
						dismissLoading();
						if(state.rootView != null && state.rootView.getVisibility() != View.VISIBLE) {
							state.rootView.setVisibility(View.VISIBLE);
						}
					}
				}
		);
	}

	protected void setViewValue(View view, Object value) {
		if(view instanceof TextView) {
			((TextView)view).setText(value == null ? "" : String.valueOf(value));
		} else if(view instanceof ListView) {
			if(value instanceof Collection) {
				ListView listView = (ListView)view;
				final UmonAdapter adapter = new UmonAdapter((Collection)value, this.getContext());
				listView.setAdapter(adapter);
			}
		} else if(view instanceof UmonCollectionView && value instanceof Collection) {
			((UmonCollectionView)view).setModels((Collection)value);
		} else if(view instanceof UmonScrollView && value instanceof Collection) {
			((UmonScrollView)view).setModels((Collection)value);
		}
	}

	protected static class State {
		//Should get these from attributes
		public int layoutId = 0;
		public boolean isViewInList = false;
		public int loadingLayoutId = 0;
		public boolean useDialogForLoading = false;

		public View rootView = null;
		public View loadingView = null;
		public Dialog dialog = null;

		public Class<? extends Model> modelClass = null;
		public Map<Method, View> mapViews = new HashMap<Method, View>();

		private final UmonView umonView;
		private Model renderdModel = null;

		public State(UmonView umonView) {
			this.umonView = umonView;
		}

		//TODO: need pass Model to param of this method
		public void initMapViews(final Model model) {
			if(model == null || rootView == null || !mapViews.isEmpty()) {
				return;
			}

			modelClass = model.getClass();
			Set<Field> fields = ReflectUtil.getAllField(modelClass);
			for(Field field : fields) {
				Method getter = ReflectUtil.getterMethod(field, modelClass);
				if(getter != null) {
					getter.setAccessible(true);
					UIView annotation = getter.getAnnotation(UIView.class);
					if(annotation == null) annotation = field.getAnnotation(UIView.class);
					if(annotation != null) {
						for(int id : annotation.views()) {
							View v = this.rootView.findViewById(id);
							if(v != null) {
								this.mapViews.put(getter, v);
							}
						}
					}
				}
			}
		}
		public void render(final Model model) {
			if(renderdModel == model || model == null) {
				return;
			} else {
				renderdModel = model;
			}

			if(renderdModel != null) {
				if(modelClass == null) {
					initMapViews(model);
				}

				if(modelClass == renderdModel.getClass() && !mapViews.isEmpty()) {
					for(Method m : mapViews.keySet()) {
						try {
							Object val = m.invoke(umonView.model);
							umonView.setViewValue(mapViews.get(m), val);
						} catch (Throwable ex) {
							Log.e(TAG, "Exception", ex);
						}
					}
				}
			}
		}
	}
}

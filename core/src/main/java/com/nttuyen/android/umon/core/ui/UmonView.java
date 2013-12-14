package com.nttuyen.android.umon.core.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
		//this.showLoading();
	}

	public Model getModel() {
		return this.model;
	}
	public void setModel(final Model model) {
		if(model == null || model == this.model) return;
		this.model = model;

		if(state.current == State.STATE_CONTENT_READY) {
			state.current = State.STATE_VIEW_READY;
		}

		//Process model in background
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
				state.initMapViews();
			}
		} catch (Throwable ex) {
			Log.e(TAG, "Exception when set model", ex);
		}
	}

	@ModelEventListener(events = {Model.ON_PROCESS_START})
	public void showLoading() {
		if(state.current == State.STATE_LOADING) {
			return;
		}

		if(state.loadingView == null) {
			if(state.loadingLayoutId > 0) {
				state.loadingView = this.inflater.inflate(state.loadingLayoutId, null);
			} else {
				state.loadingView = new ProgressBar(this.getContext());
			}
		}

		if(state.useDialogForLoading) {
			if(state.dialog == null) {
				state.dialog = new Dialog(this.getContext());
				state.dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				state.dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
				state.dialog.setContentView(state.loadingView);
				state.dialog.setCancelable(false);
			}
			state.dialog.show();
		} else {
			this.removeAllViews();
			RelativeLayout.LayoutParams params;
			if(state.loadingView instanceof ProgressBar) {
				params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
				params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
			} else {
				params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			}
			this.addView(state.loadingView, params);
		}
		state.current = State.STATE_LOADING;
	}
	protected void dismissLoading() {
		if(state.current != State.STATE_LOADING) {
			return;
		}
		final UmonView v = this;
		((Activity)this.getContext()).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(state.useDialogForLoading) {
					state.dialog.dismiss();
				} else {
					v.removeAllViews();
				}
			}
		});
		state.current = State.STATE_INIT;
	}

	protected void showBodyView() {
		if(state.current >= State.STATE_VIEW_READY || state.rootView == null) {
			return;
		}
		dismissLoading();
		final UmonView v = this;
		final RelativeLayout.LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		((Activity)this.getContext()).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				v.addView(state.rootView, params);
			}
		});
		state.current = State.STATE_VIEW_READY;
	}

	@ModelEventListener(events = {Model.ON_PROCESS_COMPLETED, Model.ON_PROCESS_ERROR})
	public void renderContent() {
		try {
			if(state.current == State.STATE_CONTENT_READY) {
				return;
			}
			//TODO: when call state.render()?
			state.render();
			if(state.current != State.STATE_VIEW_READY) {
				UmonView.this.showBodyView();
			}
			state.current = State.STATE_CONTENT_READY;
		} catch (Throwable ex) {
			Log.e(TAG, "Exception", ex);
		}
	}

	public void setLayout(int layoutId) {
		if(layoutId == 0 || layoutId == state.layoutId) {
			return;
		}

		state.layoutId = layoutId;
		state.rootView = this.inflater.inflate(layoutId, null);
		if(state.current >= State.STATE_VIEW_READY) {
			this.removeAllViews();
			this.addView(state.rootView);
		}

		state.mapViews.clear();
		state.initMapViews();

		//Init Event handler
		Context context = UmonView.this.getContext();
		if(context instanceof Activity) {
			UIEvents.on(state.rootView, context);
		}
		UIEvents.on(state.rootView, this);
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
		public static final byte STATE_INIT = 0;
		public static final byte STATE_LOADING = 1;
		public static final byte STATE_VIEW_READY = 2;
		public static final byte STATE_CONTENT_READY = 3;
		public byte current = STATE_INIT;

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

		public State(UmonView umonView) {
			this.umonView = umonView;
		}

		public void initMapViews() {
			if(umonView.model == null || rootView == null || !mapViews.isEmpty()) {
				return;
			}

			modelClass = umonView.model.getClass();
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
		public void render() {
			if(umonView.model != null) {
				if(modelClass == null) {
					initMapViews();
				}

				if(modelClass == umonView.model.getClass() && !mapViews.isEmpty()) {
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

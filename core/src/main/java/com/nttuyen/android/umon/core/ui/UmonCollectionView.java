package com.nttuyen.android.umon.core.ui;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import com.nttuyen.android.umon.core.Callback;
import com.nttuyen.android.umon.core.async.Async;
import com.nttuyen.android.umon.core.mvc.Collection;
import com.nttuyen.android.umon.core.mvc.Model;
import com.nttuyen.android.umon.core.mvc.ModelEventListener;
import com.nttuyen.android.umon.core.mvc.ModelEvents;

/**
 * @author nttuyen266@gmail.com
 */
public class UmonCollectionView extends LinearLayout {
	private Collection<? extends Model> models = null;
	private LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
	private ProgressBar progressBar = null;

	public UmonCollectionView(Context context) {
		super(context);
		this.init(context, null);
	}

	public UmonCollectionView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.init(context, null);
	}

	protected void init(Context context, AttributeSet attrs) {
		this.setOrientation(LinearLayout.VERTICAL);
		//this.removeAllViews();
	}

	public void setModels(Collection<? extends Model> collection) {
		Collection old = this.models;
		this.models = collection;
		if(old != null) {
			this.removeAllViews();
		}
		ModelEvents.on(this.models, this);
		this.render();
	}
	public Collection<? extends Model> getModels() {
		return this.models;
	}

	public void render() {
		final Activity activity = (Activity)this.getContext();
		if(this.models != null) {
			for(final Model m : models) {
				final UmonView view = new UmonView(this.getContext(), true);
				this.addView(view, params);
				Async.execute(this.toString(), new Callback() {
					@Override
					public void execute(Object... params) {
						view.setModel(m);
						activity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								view.renderContent();
							}
						});
					}
				});
			}
		}
	}

	@Override
	public void addView(View child, ViewGroup.LayoutParams params) {
		super.addView(child, params);
		if(child instanceof ProgressBar) {
			this.progressBar = (ProgressBar)child;
		}
	}

	@ModelEventListener(events = {Collection.ON_ADD})
	public void add(final Collection collection, final Model model, final int index) {
		final UmonView view = new UmonView(this.getContext(), true);
		this.addView(view, index, params);
		Async.execute(this.toString(), new Callback() {
			@Override
			public void execute(Object... params) {
				view.setModel(model);
				((Activity)view.getContext()).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						view.renderContent();
					}
				});
			}
		});
	}

	@ModelEventListener(events = {Collection.ON_ADD_ALL})
	public void addAll(final Collection collection, final Model[] models, final int fromIndex, int size) {
		final Activity activity = (Activity)this.getContext();
		final UmonView[] views = new UmonView[size];
		final int endIndex = fromIndex + size;
		final UmonCollectionView _this = this;
		Async.execute(this.toString(), new Callback() {
			@Override
			public void execute(Object... params) {
				int index = 0;
				for(int i = fromIndex; i < endIndex; i++) {
					views[index] = new UmonView(activity, true);
					views[index].setModel(collection.get(i));
					views[index].renderContent();
					index++;
				}

				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						int index = 0;
						for(int i = fromIndex; i < endIndex; i++) {
							if(views[index] != null) {
								_this.addView(views[index], i);
								index++;
							}
						}
						if(_this.progressBar != null && _this.progressBar.getVisibility() == View.VISIBLE) {
							_this.progressBar.setVisibility(View.GONE);
						}
					}
				});
			}
		});
	}

	@ModelEventListener(events = {Collection.ON_REMOVE})
	public void remove(final Collection collection, final Model model, final int index) {
		final UmonCollectionView v = this;
		((Activity) this.getContext()).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				v.removeViewAt(index);
			}
		});
	}
}

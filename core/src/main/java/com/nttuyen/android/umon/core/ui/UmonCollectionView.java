package com.nttuyen.android.umon.core.ui;

import android.R;
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
	private int renderedIndex = 0;
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
		this.progressBar = new ProgressBar(context);
		this.addView(this.progressBar, params);
	}

	public ProgressBar getProgressBar() {
		return this.progressBar;
	}

	public void setModels(Collection<? extends Model> collection) {
		Collection old = this.models;
		this.models = collection;
		this.renderedIndex = 0;
		if(old != null) {
			this.removeAllViews();
			this.addView(this.progressBar, params);
		}
		ModelEvents.on(this.models, this);
		this.render();
	}

	@ModelEventListener(events = {Collection.ON_ADD_ALL})
	public void render() {
		final Activity activity = (Activity)this.getContext();
		if(this.models != null) {
			final int startIndex = this.renderedIndex;
			final int size = models.size();
			if(size <= this.renderedIndex) {
				return;
			}
			final UmonView[] views = new UmonView[size - this.renderedIndex];
			Async.execute(
					//TODO: show one progress bar when adding content
					new Callback() {
						@Override
						public void execute(Object... params) {
							if(progressBar != null && progressBar.getVisibility() != View.VISIBLE) {
								progressBar.setVisibility(View.VISIBLE);
							}
						}
					},
					new Callback() {
						@Override
						public void execute(Object... params) {
							int index = 0;
							for(int i = startIndex; i < size; i++) {
								views[index] = new UmonView(UmonCollectionView.this.getContext(), true);
								views[index].setModel(models.get(i));
								views[index].renderContent();
								index++;
							}
						}
					},
					new Callback() {
						@Override
						public void execute(Object... params) {
							int index = 0;
							for(int i = startIndex; i < size; i++) {
								if(views[index] != null) {
									//view.renderContent();
									UmonCollectionView.this.addView(views[index], i, UmonCollectionView.this.params);
									index++;
								}
							}
							if(progressBar != null && progressBar.getVisibility() != View.GONE) {
								progressBar.setVisibility(View.GONE);
							}
						}
					}
			);
		}
	}

	@ModelEventListener(events = {Collection.ON_ADD})
	public void add(final Collection collection, final Model model, final int index) {
		final UmonView view = new UmonView(this.getContext(), true);
		this.addView(view, index, params);
		Async.execute(new Callback() {
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

	@Deprecated
	public void addAll(final Collection collection, final Model[] models, final int fromIndex, int size) {
		final Activity activity = (Activity)this.getContext();
		final UmonView[] views = new UmonView[size];
		final int endIndex = fromIndex + size;
		final UmonCollectionView _this = this;
		Async.execute(
				//Before
				null,
				//Main: what is execute on backgound
				new Callback() {
					@Override
					public void execute(Object... params) {
						int index = 0;
						for(int i = fromIndex; i < endIndex; i++) {
							views[index] = new UmonView(activity, true);
							views[index].setModel(collection.get(i));
							views[index].renderContent();
							index++;
						}
					}
				},
				//After: what is execute on UIThread
				new Callback() {
					@Override
					public void execute(Object... params) {
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
				}
		);
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

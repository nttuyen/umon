package com.nttuyen.android.umon.core.ui;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import com.nttuyen.android.umon.core.mvc.Collection;
import com.nttuyen.android.umon.core.mvc.Model;

import java.util.List;

/**
 * @author nttuyen266@gmail.com
 */
public class UmonScrollView extends ScrollView {

	protected Collection<Model> collectionViewModel = null;
	protected UmonCollectionView collectionView = null;

	protected Collection<? extends Model> models = null;

	protected ProgressBar progressBar = null;
	private LinearLayout.LayoutParams params = null;

	private int stepToAdd = 10;
	private int currentIndex = 0;

	public UmonScrollView(Context context) {
		super(context);
		this.init(context, null, 0);
	}

	public UmonScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.init(context, attrs, 0);
	}

	public UmonScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.init(context, attrs, defStyle);
	}

	private void init(Context context, AttributeSet attrs, int defStyle) {
		this.collectionView = new UmonCollectionView(this.getContext());

		LayoutParams params1 = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		this.params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		this.addView(this.collectionView, params1);

		this.progressBar = new ProgressBar(context);
	}

	public void setModels(Collection<? extends Model> models) {
		this.models = models;
		this.currentIndex = 0;

		this.collectionViewModel = new Collection<Model>();
		this.collectionView.removeAllViews();
		this.collectionView.setModels(this.collectionViewModel);

		((Activity)this.getContext()).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				collectionView.addView(progressBar, params);
				progressBar.setVisibility(View.VISIBLE);
			}
		});
		//this.collectionView.removeView(this.progressBar);

		//this.addMoreView(this.models.size() - 1);
		this.addMoreView(this.currentIndex + this.stepToAdd * 2);
	}
	public Collection<? extends Model> getModels() {
		return this.models;
	}

	public void addMoreView(int toIndex) {
		if(toIndex > this.models.size()) {
			toIndex = this.models.size();
		}

		if(toIndex <= this.currentIndex) {
			return;
		}

		List<? extends Model> c = this.models.subList(this.currentIndex, toIndex);
		this.currentIndex = toIndex;
		this.collectionViewModel.addAll(c);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		View view = this.getChildAt(this.getChildCount() - 1);
		int diff = (view.getBottom() - (this.getHeight() + this.getScrollY()));

		// if diff is zero, then the bottom has been reached
		if (diff == 0) {
			if(progressBar.getVisibility() != View.VISIBLE && this.currentIndex < models.size()) {
				progressBar.setVisibility(View.VISIBLE);
				final UmonScrollView _this = this;
				this.post(new Runnable() {
					@Override
					public void run() {
						_this.fullScroll(View.FOCUS_DOWN);
					}
				});
				this.addMoreView(this.currentIndex + this.stepToAdd);
			}
		}
	}
}

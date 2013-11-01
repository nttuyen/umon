package com.nttuyen.android.umon.core.ui;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import com.nttuyen.android.umon.core.mvc.Events;
import com.nttuyen.android.umon.core.mvc.Model;
import com.nttuyen.android.umon.core.mvc.Presenter;

/**
 * @author nttuyen266@gmail.com
 */
public abstract class ModelFragmentActivity extends FragmentActivity implements Presenter {
	protected final UIContextHelper contextHelper = new UIContextHelper(this);
	protected View bodyView = null;
	protected Model model = null;

	public void setModel(Model model) {
		this.model = model;
		Events.registerAllEvents(model, this);
	}

	@Override
	public Model getModel() {
		return this.model;
	}

	public void setBodyView(View bodyView) {
		this.bodyView = bodyView;
		UIEvents.on(this.bodyView, this);
	}
	public void setBodyView(int layout) {
		this.bodyView = getLayoutInflater().inflate(layout, null);
		UIEvents.on(this.bodyView, this);
	}

	@Override
	public View findViewById(int id) {
		View view = super.findViewById(id);
		if(view == null) {
			if(this.bodyView != null) {
				view = this.bodyView.findViewById(id);
			}
		}
		return view;
	}

	@Override
	public View getView() {
		return this.bodyView;
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		UIEvents.on(this, this);
	}
}

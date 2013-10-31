package com.nttuyen.android.umon.core.ui;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import com.nttuyen.android.umon.core.mvc.Events;
import com.nttuyen.android.umon.core.mvc.Model;
import com.nttuyen.android.umon.core.mvc.Presenter;

/**
 * @author nttuyen266@gmail.com
 */
public abstract class AnnotationFragmentActivity extends FragmentActivity implements Presenter {
	protected final UIContextHelper contextHelper = new UIContextHelper(this);
	protected View bodyView = null;
	protected Model model = null;

	public void setModel(Model model) {
		this.model = model;
		this.registerModelEvents();
	}

	@Override
	public Model getModel() {
		return this.model;
	}

	public void setBodyView(View bodyView) {
		this.bodyView = bodyView;
		registerOnClicks();
	}
	public void setBodyView(int layout) {
		this.bodyView = getLayoutInflater().inflate(layout, null);
		registerOnClicks();
	}

	@Override
	public View getView() {
		return this.bodyView;
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		UIEvents.onClick(this, this);
	}

	protected void registerModelEvents() {
		Model model = this.getModel();
		if(model == null) {
			return;
		}
		Events.registerAllEvents(model, this);
	}

	protected void registerOnClicks() {
		View view = this.getView();
		if(view != null) {
			UIEvents.onClick(view, this);
		}
	}
}

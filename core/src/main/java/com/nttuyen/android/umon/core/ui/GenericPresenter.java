package com.nttuyen.android.umon.core.ui;

import android.app.Activity;
import android.view.View;
import com.nttuyen.android.umon.core.mvc.Events;
import com.nttuyen.android.umon.core.mvc.Model;
import com.nttuyen.android.umon.core.mvc.Presenter;

/**
 * @author nttuyen266@gmail.com
 */
public class GenericPresenter implements Presenter {
	protected final Model model;
	protected final View view;

	/**
	 * TODO: need this method?
	 * @param model
	 * @param activity
	 */
	public GenericPresenter(Model model, Activity activity) {
		this(model, activity.getWindow().getDecorView());
	}

	public GenericPresenter(Model model, View view) {
		this.model = model;
		this.view = view;

		UIEvents.on(this.view, this);
		Events.registerAllEvents(this.model, this);
	}

	@Override
	public View getView() {
		return this.view;
	}

	@Override
	public Model getModel() {
		return this.model;
	}
}

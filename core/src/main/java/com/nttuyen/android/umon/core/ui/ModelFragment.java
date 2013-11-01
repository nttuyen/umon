package com.nttuyen.android.umon.core.ui;

import android.support.v4.app.Fragment;
import com.nttuyen.android.umon.core.mvc.Events;
import com.nttuyen.android.umon.core.mvc.Model;
import com.nttuyen.android.umon.core.mvc.Presenter;

/**
 * @author nttuyen266@gmail.com
 */
public class ModelFragment extends Fragment implements Presenter {
	protected Model model;

	@Override
	public Model getModel() {
		return this.model;
	}
	public void setModel(Model model) {
		this.model = model;
		Events.registerAllEvents(this.model, this);
	}
}

package com.nttuyen.android.umon.core.ui;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.nttuyen.android.umon.core.mvc.ModelEvents;
import com.nttuyen.android.umon.core.mvc.Model;

/**
 * @author nttuyen266@gmail.com
 */
@Deprecated
public abstract class UmonAdapter extends BaseAdapter {
	protected final Model.Collection<T> models;

	public UmonAdapter(Model.Collection<T> models) {
		this.models = models;
		ModelEvents.on(this.models, this);
	}

	@Override
	public int getCount() {
		return models.size();
	}

	@Override
	public Object getItem(int i) {
		return models.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {

	}
}

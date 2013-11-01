package com.nttuyen.android.umon.core.ui;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import com.nttuyen.android.umon.core.mvc.Events;
import com.nttuyen.android.umon.core.mvc.Model;
import com.nttuyen.android.umon.core.mvc.Presenter;

/**
 * @author nttuyen266@gmail.com
 */
public abstract class ModelAdapter<T extends Model> extends BaseAdapter {
	protected final Model.Collection<T> models;

	public ModelAdapter(Model.Collection<T> models) {
		this.models = models;
		Events.registerAllEvents(this.models, this);
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
		return models.get(i).getId();
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		return getPresenter(i, view, viewGroup).getView();
	}

	public abstract Presenter getPresenter(int i, View view, ViewGroup viewGroup);
}

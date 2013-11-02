package com.nttuyen.android.umon.core.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.nttuyen.android.umon.core.mvc.Events;
import com.nttuyen.android.umon.core.mvc.Model;
import com.nttuyen.android.umon.core.mvc.ModelEventListener;

/**
 * @author nttuyen266@gmail.com
 */
public abstract class ModelFragmentPagerAdapter<T extends Model> extends FragmentPagerAdapter {
	protected Model.Collection<T> models;

	public ModelFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		return this.getFragment(position);
	}

	@Override
	public int getCount() {
		return models != null ? models.size() : 0;
	}

	public Model.Collection<T> getModels() {
		return this.models;
	}
	public void setModels(Model.Collection<T> models) {
		this.models = models;
		Events.registerAllEvents(this.models, this);
	}

	public abstract ModelFragment getFragment(int position);

	@ModelEventListener(events = {Model.ON_CHANGE})
	public void onChange() {
		notifyDataSetChanged();
	}
}

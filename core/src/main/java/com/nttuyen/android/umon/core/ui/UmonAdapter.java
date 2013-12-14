package com.nttuyen.android.umon.core.ui;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.nttuyen.android.umon.core.Callback;
import com.nttuyen.android.umon.core.async.Async;
import com.nttuyen.android.umon.core.mvc.Collection;
import com.nttuyen.android.umon.core.mvc.ModelEvents;
import com.nttuyen.android.umon.core.mvc.Model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author nttuyen266@gmail.com
 */
public class UmonAdapter<M extends Model> extends BaseAdapter {
	protected final Collection<M> collection;
	protected final Context context;

	public UmonAdapter(Collection<M> collection, Context context) {
		this.collection = collection;
		this.context = context;
		ModelEvents.on(this.collection, this);
	}

	@Override
	public int getCount() {
		return collection.size();
	}

	@Override
	public Object getItem(int i) {
		return collection.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int i, View convertView, ViewGroup viewGroup) {
		final Model m = this.collection.get(i);

		UmonView view;
		if(convertView != null && convertView instanceof UmonView) {
			view = (UmonView)convertView;
			//view.showLoading();
		} else {
			view = new UmonView(context, true);
		}

		maps.put(view, m);
		this.isScrolling = true;

		if(task == null) {
			task = new AsyncTask() {
				@Override
				protected Object doInBackground(Object... params) {
					try {
						while(!maps.isEmpty()) {
							while(UmonAdapter.this.isScrolling) {
								UmonAdapter.this.isScrolling = false;
								Thread.sleep(100);
							}
							final UmonView v = maps.keySet().iterator().next();
							final Model mo = maps.remove(v);
							((Activity)v.getContext()).runOnUiThread(new Runnable() {
								@Override
								public void run() {
									v.setModel(mo);
									v.renderContent();
								}
							});
						}
					} catch (Throwable ex) {

					} finally {
						UmonAdapter.this.task = null;
					}
					return null;
				}
			};
			task.execute();
		}

		//view.setModel(m);
		//view.renderContent();

		return view;
	}

	protected final Map<UmonView, Model> maps = new ConcurrentHashMap<UmonView, Model>();
	protected boolean isScrolling = false;
	protected AsyncTask task = null;
}

package com.nttuyen.android.umon.app.model;

import android.os.AsyncTask;
import com.nttuyen.android.umon.app.R;
import com.nttuyen.android.umon.core.mvc.Collection;
import com.nttuyen.android.umon.core.mvc.Model;
import com.nttuyen.android.umon.core.ui.UILayout;
import com.nttuyen.android.umon.core.ui.UIView;

/**
 * @author nttuyen266@gmail.com
 */
@UILayout(
	layout = R.layout.model_listview,
	layoutInListView = 0
)
public class ListModel extends Model {
	@UIView(views = {R.id.title})
	private String name;

	@UIView(views = {R.id.listView})
	private Collection<TestModel> collection = new Collection<TestModel>();

	public String getName() {
		return this.name;
	}
	public Collection<TestModel> getCollection() {
		return this.collection;
	}

	@Override
	public void fetch() {
		AsyncTask task = new AsyncTask() {
			@Override
			protected Object doInBackground(Object... objects) {
				try {
					Thread.sleep(10000);
					name = "Demo xem sao thoi";
					for(int i = 0; i < 100; i++) {
						TestModel m = new TestModel();
						m.name = "Value " + i;
						collection.add(m);
					}
				} catch (Exception ex) {
					System.out.println("Error");
				}
				return null;
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				trigger(Model.ON_PROCESS_START, ListModel.this, "test");
			}

			@Override
			protected void onPostExecute(Object o) {
				super.onPostExecute(o);
				trigger(Model.ON_PROCESS_COMPLETED, ListModel.this, "test");
			}
		};
		task.execute();
	}

	@Override
	public void save() {

	}
}

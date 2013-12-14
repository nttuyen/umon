package com.nttuyen.android.umon.app.model;

import android.os.AsyncTask;
import com.nttuyen.android.umon.app.R;
import com.nttuyen.android.umon.core.mvc.Model;
import com.nttuyen.android.umon.core.ui.UILayout;
import com.nttuyen.android.umon.core.ui.UIView;

/**
 * @author nttuyen266@gmail.com
 */
@UILayout(
	layout = 0,
	layoutInListView = R.layout.model_test_view
)
public class TestModel extends Model {

	@UIView(views = {R.id.testView})
	public String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void fetch() {
		AsyncTask task = new AsyncTask() {
			@Override
			protected Object doInBackground(Object... objects) {
				try {
					Thread.sleep(10000);
					name = "Demo xem sao thoi";
				} catch (Exception ex) {
					System.out.println("Error");
				}
				return null;
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				trigger(Model.ON_PROCESS_START, TestModel.this, "test");
			}

			@Override
			protected void onPostExecute(Object o) {
				super.onPostExecute(o);
				trigger(Model.ON_PROCESS_COMPLETED, TestModel.this, "test");
			}
		};
		task.execute();
	}

	@Override
	public void save() {

	}
}

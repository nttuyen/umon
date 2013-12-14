package com.nttuyen.android.umon.app.activity;

import com.nttuyen.android.umon.app.model.ListModel;
import com.nttuyen.android.umon.app.model.TestModel;
import com.nttuyen.android.umon.core.mvc.Model;

/**
 * @author nttuyen266@gmail.com
 */
public class HomeActivity extends BaseActivity {
	@Override
	protected void onStart() {
		super.onStart();
		Model m = new ListModel();
		this.bodyView.setModel(m);
		m.fetch();
	}
}

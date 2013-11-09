package com.nttuyen.android.umon.app.activity;

import android.app.ActionBar;
import android.os.Bundle;
import com.nttuyen.android.umon.app.R;
import com.nttuyen.android.umon.core.ui.ModelActivity;

/**
 * @author nttuyen266@gmail.com
 */
public abstract class BaseActivity extends ModelActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}
}

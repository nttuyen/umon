package com.nttuyen.android.umon.app.activity;

import android.app.Activity;
import android.os.Bundle;
import com.nttuyen.android.umon.app.R;
import com.nttuyen.android.umon.core.ui.UmonView;

/**
 * @author nttuyen266@gmail.com
 */
public abstract class BaseActivity extends Activity {
	protected UmonView bodyView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		this.bodyView = (UmonView)this.findViewById(R.id.bodyView);
	}
}

package com.nttuyen.android.umon.core.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * @author nttuyen266@gmail.com
 */
public class UI {
	public static void go(Context from, Class<? extends Activity> next, Bundle extra) {
		if(from == null || next == null) {
			return;
		}
		Intent intent = new Intent(from, next);
		if(extra != null) {
			intent.putExtras(extra);
		}
		from.startActivity(intent);
	}
}

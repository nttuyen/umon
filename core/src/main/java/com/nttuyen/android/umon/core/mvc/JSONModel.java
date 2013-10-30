package com.nttuyen.android.umon.core.mvc;

import com.nttuyen.android.umon.core.Callback;
import com.nttuyen.android.umon.core.async.Async;
import com.nttuyen.android.umon.core.http.HTTP;
import com.nttuyen.android.umon.core.http.JsonResponse;
import com.nttuyen.android.umon.core.json.JsonConvertHelper;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

/**
 * @author nttuyen266@gmail.com
 */
public abstract class JSONModel extends Model {
	public static final String PROCESS_HTTP_REQUEST = "http_request";

	protected JsonResponse response = null;
	/**
	 * URL for get data
	 * @return
	 */
	public abstract String url();

	@Override
	public void fetch() {
		String url = this.url();

		if(url == null) {
			trigger(ON_PROCESS_COMPLETED, this, PROCESS_HTTP_REQUEST);
		}

		response = new JsonResponse();

		Map<String, Object> options = new HashMap<String, Object>();
		options.put(Async.OPTION_TASK_NAME, PROCESS_HTTP_REQUEST);
		options.put(Async.OPTION_ON_PRE_EXECUTE, Callback.MethodCallback.newInstance("preAsync", this));
		//options.put(Async.OPTION_ON_POST_EXECUTE, postAsync);
		options.put(HTTP.OPTION_SUCCESS, Callback.MethodCallback.newInstance("onHttpSuccess", this));
		options.put(HTTP.OPTION_ERROR, Callback.MethodCallback.newInstance("onHttpFailure", this));

		Async.http(url, options);
	}

	protected void fromJson(JSONObject json) {
		try {
			JsonConvertHelper.inject(json, this);
			trigger(ON_PROCESS_COMPLETED, this, PROCESS_HTTP_REQUEST);
		} catch (Exception ex) {
			trigger(ON_PROCESS_ERROR, JSONModel.this, PROCESS_HTTP_REQUEST, 409, "Conflict", "Response message is not as expected");
		}
	}

	//Pre async execute
	public void preAsync(String taskName) {
		trigger(ON_PROCESS_START, JSONModel.this, taskName);
	}
	//Post async execute

	//HTTP success
	public void onHttpSuccess() {
		fromJson(response.getResult());
	}

	//Http failure
	public void onHttpFailure(int code, String status, String message) {
		trigger(ON_PROCESS_ERROR, JSONModel.this, PROCESS_HTTP_REQUEST, code, status, message);
	}
}

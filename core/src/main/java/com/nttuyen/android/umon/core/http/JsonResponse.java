package com.nttuyen.android.umon.core.http;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author nttuyen266@gmail.com
 */
public class JsonResponse extends Response<JSONObject>  {
	private JSONObject result = null;
	@Override
	public void parse(InputStream input) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		StringBuilder builder = new StringBuilder();

		String line = null;
		while((line = reader.readLine()) != null) {
			builder.append(line);
		}

		this.result = new JSONObject(builder.toString());
	}

	@Override
	public JSONObject getResult() {
		return this.result;
	}
}

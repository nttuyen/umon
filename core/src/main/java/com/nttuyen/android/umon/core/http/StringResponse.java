package com.nttuyen.android.umon.core.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author nttuyen266@gmail.com
 */
public class StringResponse extends Response<String> {
	String result = null;

	@Override
	public void parse(InputStream input) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		StringBuilder builder = new StringBuilder();

		String line = null;
		while((line = reader.readLine()) != null) {
			builder.append(line);
		}

		this.result = builder.toString();
	}

	public String getResult() {
		return this.result;
	}
}

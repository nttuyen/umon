package com.nttuyen.android.umon.core.mapper;

import org.json.JSONObject;

/**
 * @author nttuyen266@gmail.com
 */
public class ToJSONObjectMapper<Source> implements ModelMapper<Source, JSONObject> {
	@Override
	public JSONObject map(Source source, JSONObject target) {
		if(source == null || target == null) {
			return target;
		}

		Class sourceType = source.getClass();
		//TODO: process to JSON

		return target;
	}
}

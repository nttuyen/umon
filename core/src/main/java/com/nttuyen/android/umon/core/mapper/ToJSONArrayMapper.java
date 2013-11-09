package com.nttuyen.android.umon.core.mapper;

import org.json.JSONArray;

/**
 * @author nttuyen266@gmail.com
 */
public class ToJSONArrayMapper<Source> implements Mapper<Source, JSONArray> {
	@Override
	public JSONArray map(Source source, JSONArray target) {
		if(source == null || target == null) {
			return target;
		}

		//TODO: process to json array

		return target;
	}
}

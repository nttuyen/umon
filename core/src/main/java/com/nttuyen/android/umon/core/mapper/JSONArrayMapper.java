package com.nttuyen.android.umon.core.mapper;

import android.util.Log;
import com.nttuyen.android.umon.core.json.JSONUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;

/**
 * @author nttuyen266@gmail.com
 */
public class JSONArrayMapper<Target> implements ModelMapper<JSONArray, Target> {
	private static final String TAG = "[umon][core]" + JSONArrayMapper.class.getName();

	private JSONObjectMapper jsonObjectMapper = null;

	public JSONArrayMapper() {

	}

	void setJsonObjectMapper(JSONObjectMapper mapper) {
		this.jsonObjectMapper = mapper;
	}

	@Override
	public Target map(JSONArray source, Target target) {
		if(source == null || target == null || !target.getClass().isArray()) {
			return target;
		}
		prepairJSONObjectMapper();

		Class targetClass = target.getClass();
		Class targetType = targetClass.getComponentType();

		int sourceSize = source.length();

		int targetSize = Array.getLength(target);
		if(targetSize == 0) {
			target = (Target)Array.newInstance(targetType, sourceSize);
			targetSize = Array.getLength(target);
		}

		boolean isPrimitive = JSONUtil.isPrimaryType(targetType);

		int size = sourceSize < targetSize ? sourceSize : targetSize;
		for(int i = 0; i < size; i++) {
			if(isPrimitive) {
				processPrimaryElement(source, target, targetType, i);
			} else {
				processObjectElement(source, target, targetType, i);
			}
		}

		return target;
	}

	private void processObjectElement(JSONArray source, Target target, Class targetType, int index) {
		Object obj = Array.get(target, index);
		if(obj == null) {
			try {
				obj = targetType.newInstance();
			} catch (InstantiationException e) {
				Log.e(TAG, "InstantiationException", e);
				obj = null;
			} catch (IllegalAccessException e) {
				Log.e(TAG, "IllegalAccessException", e);
				obj = null;
			}
		}

		if(obj != null) {
			try {
				JSONObject jsonObject = source.getJSONObject(index);
				obj = this.jsonObjectMapper.map(jsonObject, obj);
				Array.set(target, index, obj);
			} catch (JSONException e) {
				Log.e(TAG, "JSONException", e);
			}
		}
	}

	private void processPrimaryElement(JSONArray source, Target target, Class targetType, int index) {
		try {
			Array.set(target, index, JSONUtil.getPrimaryValue(source, index, targetType));
		} catch (JSONException ex) {
			Log.e(TAG, "JSONException", ex);
		}
	}

	private void prepairJSONObjectMapper() {
		if(this.jsonObjectMapper == null) {
			this.jsonObjectMapper = new JSONObjectMapper();
			this.jsonObjectMapper.setJsonArrayMapper(this);
		}
	}
}

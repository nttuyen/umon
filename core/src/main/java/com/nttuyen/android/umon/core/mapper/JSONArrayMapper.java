package com.nttuyen.android.umon.core.mapper;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * @author nttuyen266@gmail.com
 */
public class JSONArrayMapper<Target> implements Mapper<JSONArray, Target> {
	private static final String TAG = "[umon][core]" + JSONArrayMapper.class.getName();

	private JSONObjectMapper jsonObjectMapper = null;

	public JSONArrayMapper() {

	}

	void setJsonObjectMapper(JSONObjectMapper mapper) {
		this.jsonObjectMapper = mapper;
	}

	@Override
	public Target map(JSONArray source, Target target) {

		if(source == null || target == null) {
			return target;
		}

		if(this.jsonObjectMapper == null) {
			this.jsonObjectMapper = new JSONObjectMapper();
			this.jsonObjectMapper.setJsonArrayMapper(this);
		}

		Class targetClass = target.getClass();
		if(!targetClass.isArray()) {
			return target;
		}

		try {
			int sourceSize = source.length();
			Class type = targetClass.getComponentType();
			int targetSize = Array.getLength(target);
			if(targetSize == 0) {
				target = (Target)Array.newInstance(type, sourceSize);
				targetSize = Array.getLength(target);
			}

			boolean isPrimitive = type.isPrimitive();

			int size = sourceSize < targetSize ? sourceSize : targetSize;
			for(int i = 0; i < size; i++) {
				if(isPrimitive) {
					Array.set(target, i, this.getPrimaryValue(source, i, type));
				} else if(type.equals(String.class)) {
					Array.set(target, i, source.getString(i));
				} else if(type.equals(JSONObject.class)) {
					Array.set(target, i, source.getJSONObject(i));
				} else if(type.equals(JSONArray.class)) {
					Array.set(target, i, source.getJSONArray(i));
				} else {
					Object obj = Array.get(target, i);
					if(obj == null) {
						try {
							obj = type.newInstance();
						} catch (InstantiationException e) {
							Log.e(TAG, "InstantiationException", e);
						} catch (IllegalAccessException e) {
							Log.e(TAG, "IllegalAccessException", e);
						}
					}

					if(obj != null) {
						try {
							JSONObject jsonObject = source.getJSONObject(i);
							obj = this.jsonObjectMapper.map(jsonObject, obj);
							Array.set(target, i, obj);
						} catch (JSONException e) {
							Log.e(TAG, "JSONException", e);
						}
					}
				}
			}
		} catch (JSONException ex) {
			Log.e(TAG, "JSONException", ex);
		}
		return target;
	}

	public <T> Object getPrimaryValue(JSONArray source, int index, Class<T> type) {
		try {
			if(!type.isPrimitive()) {
				return null;
			}

			if(type.equals(Boolean.class) || type.equals(Boolean.TYPE)) {
				return source.getBoolean(index);
			}
			if(type.equals(Double.class) || type.equals(Double.TYPE)) {
				return source.getDouble(index);
			} else if(type.equals(Float.class) || type.equals(Float.TYPE)) {
				return Float.valueOf(String.valueOf(source.getDouble(index)));
			} else if(type.equals(Integer.class) || type.equals(Integer.TYPE)) {
				return source.getInt(index);
			}
			if(type.equals(Long.class) || type.equals(Long.TYPE)) {
				return source.getLong(index);
			}

			return type.cast(source.get(index));
		} catch (Exception ex) {
			Log.e(TAG, "Exception", ex);
			return null;
		}
	}
}

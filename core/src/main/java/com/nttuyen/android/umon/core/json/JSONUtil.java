package com.nttuyen.android.umon.core.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author nttuyen266@gmail.com
 */
public class JSONUtil {
	public static boolean isPrimaryType(Class type) {
		return type != null && (
				type.isPrimitive()
				|| type.equals(String.class)
				|| type.equals(JSONObject.class)
				|| type.equals(JSONArray.class));
	}

	public static Object getPrimaryValue(JSONObject json, String fieldName, Class type) throws JSONException {
		if(!isPrimaryType(type)) return null;

		if(type.equals(Byte.class) || type.equals(Byte.TYPE)) {
			return Byte.valueOf(String.valueOf(json.getInt(fieldName)));
		}

		if(type.equals(Short.class) || type.equals(Short.TYPE)) {
			return Short.valueOf(String.valueOf(json.getInt(fieldName)));
		}
		if(type.equals(Integer.class) || type.equals(Integer.TYPE)) {
			return json.getInt(fieldName);
		}
		if(type.equals(Long.class) || type.equals(Long.TYPE)) {
			return json.getLong(fieldName);
		}
		if(type.equals(Float.class) || type.equals(Float.TYPE)) {
			return Float.valueOf(String.valueOf(json.getDouble(fieldName)));
		}
		if(type.equals(Double.class) || type.equals(Double.TYPE)) {
			return json.getDouble(fieldName);
		}
		if(type.equals(Boolean.class) || type.equals(Boolean.TYPE)) {
			return json.getBoolean(fieldName);
		}
		if(type.equals(Character.class) || type.equals(Character.TYPE)) {
			return json.getString(fieldName).charAt(0);
		}
		if(type.equals(String.class)) {
			return json.getString(fieldName);
		}
		if(type.equals(JSONObject.class)) {
			return json.getJSONObject(fieldName);
		}
		if(type.equals(JSONArray.class)) {
			return json.getJSONArray(fieldName);
		}

		return null;
	}
	public static Object getPrimaryValue(JSONArray json, int index, Class type) throws JSONException {
		if(!isPrimaryType(type)) return null;

		if(type.equals(Byte.class) || type.equals(Byte.TYPE)) {
			return Byte.valueOf(String.valueOf(json.getInt(index)));
		}

		if(type.equals(Short.class) || type.equals(Short.TYPE)) {
			return Short.valueOf(String.valueOf(json.getInt(index)));
		}
		if(type.equals(Integer.class) || type.equals(Integer.TYPE)) {
			return json.getInt(index);
		}
		if(type.equals(Long.class) || type.equals(Long.TYPE)) {
			return json.getLong(index);
		}
		if(type.equals(Float.class) || type.equals(Float.TYPE)) {
			return Float.valueOf(String.valueOf(json.getDouble(index)));
		}
		if(type.equals(Double.class) || type.equals(Double.TYPE)) {
			return json.getDouble(index);
		}
		if(type.equals(Boolean.class) || type.equals(Boolean.TYPE)) {
			return json.getBoolean(index);
		}
		if(type.equals(Character.class) || type.equals(Character.TYPE)) {
			return json.getString(index).charAt(0);
		}
		if(type.equals(String.class)) {
			return json.getString(index);
		}
		if(type.equals(JSONObject.class)) {
			return json.getJSONObject(index);
		}
		if(type.equals(JSONArray.class)) {
			return json.getJSONArray(index);
		}

		return null;
	}
}

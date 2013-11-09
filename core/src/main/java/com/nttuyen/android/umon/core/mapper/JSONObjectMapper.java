package com.nttuyen.android.umon.core.mapper;

import android.util.Log;
import com.nttuyen.android.umon.core.json.JSONUtil;
import com.nttuyen.android.umon.core.json.Json;
import com.nttuyen.android.umon.core.reflect.ReflectUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author nttuyen266@gmail.com
 */
public class JSONObjectMapper<Target> implements Mapper<JSONObject, Target> {
	private static final String TAG = "[umon][core]" + JSONObjectMapper.class.getName();

	private JSONArrayMapper jsonArrayMapper;

	public JSONObjectMapper() {

	}

	void setJsonArrayMapper(JSONArrayMapper mapper) {
		this.jsonArrayMapper = mapper;
	}

	@Override
	public Target map(JSONObject source, Target target) {
		if(source == null || target == null || target.getClass().isPrimitive()) {
			return target;
		} else if(target instanceof JSONObject) {
			return (Target)source;
		}

		repairJSONArrayMapper();
		Class targetClass = target.getClass();
		Set<Field> fields = ReflectUtil.getAllField(targetClass);
		for(Field field : fields) {
			this.processField(source, field, target, targetClass);
		}

		return target;
	}

	private void repairJSONArrayMapper() {
		if(this.jsonArrayMapper == null) {
			this.jsonArrayMapper = new JSONArrayMapper();
			this.jsonArrayMapper.setJsonObjectMapper(this);
		}
	}
	private void processField(JSONObject source, Field field, Target target, Class targetClass) {
		String jsonField = this.getJsonField(field);
		if(ReflectUtil.setterMethod(field, targetClass) == null || !source.has(jsonField)) {
			//This field is readonly (has not setter method) or has not contain the key
			return;
		}

		if(source.isNull(jsonField)) {
			ReflectUtil.setValueOfField(field, null, target);
		}

		Class fieldType = field.getType();
		Object fieldValue = null;

		if(JSONUtil.isPrimaryType(fieldType)) {
			fieldValue = processFieldWithPrimaryType(source, jsonField, fieldType, fieldValue);

		} else if(fieldType.isArray()) {
			fieldValue = processFieldWithArrayType(source, jsonField, fieldType, fieldValue);

		} else if(Collection.class.isAssignableFrom(fieldType)) {
			if(this.getJsonCollectionType(field) != null) {
				fieldValue = processFieldWithCollectionType(source, field, target, jsonField, fieldType);
			}

		} else {
			fieldValue = processFieldWithObjectType(source, field, target, jsonField, fieldType);
		}


		if(fieldValue != null) {
			ReflectUtil.setValueOfField(field, fieldValue, target);
		}
	}

	private Object processFieldWithCollectionType(JSONObject source, Field field, Target target, String jsonField, Class fieldType) {
		Object fieldValue;
		Class type = this.getJsonCollectionType(field);


		Object array = getArrayValues(source, jsonField, type);

		fieldValue = ReflectUtil.getValueOfField(field, target);
		if(fieldValue == null) {
			if(Set.class.isAssignableFrom(fieldType)) {
				fieldValue = new HashSet();
			} else {
				fieldValue = new ArrayList();
			}
		}

		if(fieldValue != null) {
			((Collection)fieldValue).clear();
			int length = Array.getLength(array);
			for(int i = 0; i < length; i++) {
				Object obj = Array.get(array, i);
				if(obj != null) {
					((Collection)fieldValue).add(obj);
				}
			}
		}
		return fieldValue;
	}

	private Object processFieldWithObjectType(JSONObject source, Field field, Target target, String jsonField, Class fieldType) {
		Object fieldValue;
		fieldValue = ReflectUtil.getValueOfField(field, target);
		if(fieldValue == null) {
			try {
				fieldValue = fieldType.newInstance();
			} catch (InstantiationException ex) {
				Log.e(TAG, "InstantiationException", ex);
				fieldValue = null;
			} catch (IllegalAccessException e) {
				Log.e(TAG, "IllegalAccessException", e);
			}
		}
		if(fieldValue != null) {
			try {
				JSONObject jsonObject = source.getJSONObject(jsonField);
				fieldValue = ((JSONObjectMapper)this).map(jsonObject, fieldValue);
			} catch (JSONException ex) {
				Log.e(TAG, "JSONException", ex);
			}
		}
		return fieldValue;
	}

	private Object getArrayValues(JSONObject source, String jsonField, Class type) {
		Object array = Array.newInstance(type, 0);
		try {
			JSONArray jsonArray = source.getJSONArray(jsonField);
			array = this.jsonArrayMapper.map(jsonArray, array);
		} catch (JSONException ex) {
			Log.e(TAG, "JSONException", ex);
		}

		return array;
	}

	private Object processFieldWithArrayType(JSONObject source, String jsonField, Class fieldType, Object fieldValue) {
		try {
			fieldValue = Array.newInstance(fieldType.getComponentType(), 0);
			JSONArray jsonArray = source.getJSONArray(jsonField);
			fieldValue = this.jsonArrayMapper.map(jsonArray, fieldValue);
		} catch (JSONException ex) {
			Log.e(TAG, "JSONException", ex);
		}
		return fieldValue;
	}

	private Object processFieldWithPrimaryType(JSONObject source, String jsonField, Class fieldType, Object fieldValue) {
		try {
			fieldValue = JSONUtil.getPrimaryValue(source, jsonField, fieldType);
		} catch (JSONException e) {
			Log.e(TAG, "JSONException", e);
		}
		return fieldValue;
	}

	private String getJsonField(Field field) {
		String jsonField = field.getName();
		Json json = field.getAnnotation(Json.class);
		if(json != null && !"".equals(json.name())) {
			jsonField = json.name();
		}

		return jsonField;
	}
	private Class getJsonCollectionType(Field field) {
		Json json = field.getAnnotation(Json.class);
		if(json != null && !json.type().equals(Object.class)) {
			return json.type();
		}
		return null;
	}
}

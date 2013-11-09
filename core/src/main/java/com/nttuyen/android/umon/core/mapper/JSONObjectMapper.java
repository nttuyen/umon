package com.nttuyen.android.umon.core.mapper;

import android.util.Log;
import com.nttuyen.android.umon.core.json.Json;
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
		if(source == null || target == null) {
			return target;
		}

		if(this.jsonArrayMapper == null) {
			this.jsonArrayMapper = new JSONArrayMapper();
			this.jsonArrayMapper.setJsonObjectMapper(this);
		}

		Class targetClass = target.getClass();
		if(targetClass.isPrimitive()) {
			return target;
		} else if(targetClass.equals(JSONObject.class)) {
			target = (Target)source;
			return target;
		}

		//TODO: need to process parent field if has
		Set<Field> fields = new HashSet<Field>();

		//Field declared in current class
		Field[] fs = targetClass.getDeclaredFields();
		for(Field f : fs) {
			fields.add(f);
		}

		Class parent = targetClass.getSuperclass();
		while (parent != null && !parent.equals(Object.class)) {
			fs = parent.getDeclaredFields();
			for(Field f : fs) {
				fields.add(f);
			}

			parent = parent.getSuperclass();
		}

		for(Field field : fields) {
			Method setter = this.setterMethod(field, targetClass);
			if(setter == null) {
				//This field is readonly
				continue;
			}
			if(!setter.isAccessible()) {
				setter.setAccessible(true);
			}

			String jsonField = field.getName();
			Json json = field.getAnnotation(Json.class);
			if(json != null && !"".equals(json.name())) {
				jsonField = json.name();
			}

			if(source.isNull(jsonField)) {
				//If has no json value for this field
				continue;
			}

			Class fieldType = field.getType();
			Object fieldValue = null;

			try {
				if(fieldType.isPrimitive()) {
					fieldValue = this.getPrimary(source, jsonField, fieldType);

				} else if(fieldType.equals(String.class) || fieldType.equals(JSONObject.class) || fieldType.equals(JSONArray.class)) {
					fieldValue = fieldType.cast(source.get(jsonField));
				} else {
					Method getter = this.getterMethod(field, targetClass);
					if(!getter.isAccessible()) {
						getter.setAccessible(true);
					}
					if(!field.isAccessible()) {
						field.setAccessible(true);
					}

					//Get current fieldValue or create new if is null
					fieldValue = getter != null ? getter.invoke(target) : field.get(target);

					if(fieldType.isArray()) {
						if(fieldValue == null) {
							fieldValue = Array.newInstance(fieldType.getComponentType(), 0);
						}

						JSONArray jsonArray = source.getJSONArray(jsonField);
						fieldValue = this.jsonArrayMapper.map(jsonArray, fieldValue);
					} else if(Collection.class.isAssignableFrom(fieldType)) {
						if(json == null) {
							//Cannot to process
							continue;
						}

						Class type = json.type();
						Object array = Array.newInstance(type, 0);
						JSONArray jsonArray = source.getJSONArray(jsonField);
						array = this.jsonArrayMapper.map(jsonArray, array);

						if(fieldValue != null) {
							((Collection)fieldValue).clear();
							int length = Array.getLength(array);
							for(int i = 0; i < length; i++) {
								Object obj = Array.get(array, i);
								if(obj != null) {
									((Collection)fieldValue).add(obj);
								}
							}
						} else {
							if(Set.class.isAssignableFrom(fieldType)) {
								//Default set instance is hashset
								fieldValue = new HashSet();
							} else {
								//Defaut for collection or list is arraylist
								fieldValue = new ArrayList();
							}

							int length = Array.getLength(array);
							for(int i = 0; i < length; i++) {
								Object obj = Array.get(array, i);
								if(obj != null) {
									((Collection)fieldValue).add(obj);
								}
							}
						}
					} else {
						if(fieldValue == null) {
							try {
								fieldValue = fieldType.newInstance();
							} catch (InstantiationException ex) {
								Log.e(TAG, "InstantiationException", ex);
								fieldValue = null;
							}
						}
						if(fieldValue == null) {
							//Only inject for a concrete type
							continue;
						}

						JSONObject jsonObject = source.getJSONObject(jsonField);
						fieldValue = ((JSONObjectMapper)this).map(jsonObject, fieldValue);
					}
				}
			} catch (ClassCastException ex) {
				Log.e(TAG, "ClassCastException", ex);
				fieldValue = null;
			} catch (JSONException ex) {
				Log.e(TAG, "JSONException", ex);
				fieldValue = null;
			} catch (InvocationTargetException e) {
				Log.e(TAG, "InvocationTargetException", e);
				fieldValue = null;
			} catch (IllegalAccessException e) {
				Log.e(TAG, "IllegalAccessException", e);
				fieldValue = null;
			}

			if(fieldValue != null) {
				try {
					setter.invoke(target, fieldValue);
				} catch (IllegalAccessException e) {
					Log.e(TAG, "IllegalAccessException", e);
				} catch (InvocationTargetException e) {
					Log.e(TAG, "InvocationTargetException", e);
				}
			}
		}

		return target;
	}

	private Method setterMethod(Field field, Class type) {
		if(type == null) {
			type = field.getDeclaringClass();
		}
		String fieldName = field.getName();
		String upper = fieldName.toUpperCase();
		StringBuilder builder = new StringBuilder();
		builder.append("set")
				.append(upper.charAt(0))
				.append(fieldName)
				.deleteCharAt(4);

		String methodName = builder.toString();
		Class fieldType = field.getType();

		try {
			Method method = type.getMethod(methodName, fieldType);
			return method;
		} catch (Exception ex) {
			return null;
		}
	}

	private Method getterMethod(Field field, Class type) {
		if(type == null) {
			type = field.getDeclaringClass();
		}
		String fieldName = field.getName();
		String upper = fieldName.toUpperCase();
		StringBuilder builder = new StringBuilder();
		builder.append("get")
				.append(upper.charAt(0))
				.append(fieldName)
				.deleteCharAt(4);

		String methodName = builder.toString();

		try {
			return type.getMethod(methodName);
		} catch (NoSuchMethodException e) {
			Log.e(TAG, "NoSuchMethodException", e);
		}

		return null;
	}

	private <T> Object getPrimary(JSONObject json, String fieldName, Class<T> type) throws JSONException {
		if(!type.isPrimitive()) {
			return null;
		}

		if(type.equals(Boolean.class) || type.equals(Boolean.TYPE)) {
			return json.getBoolean(fieldName);
		}
		if(type.equals(Double.class) || type.equals(Double.TYPE)) {
			return json.getDouble(fieldName);
		}
		if(type.equals(Float.class) || type.equals(Float.TYPE)) {
			return Float.valueOf(String.valueOf(json.getDouble(fieldName)));
		}
		if(type.equals(Integer.class) || type.equals(Integer.TYPE)) {
			return json.getInt(fieldName);
		}
		if(type.equals(Long.class) || type.equals(Long.TYPE)) {
			return json.getLong(fieldName);
		}

		return type.cast(json.getString(fieldName));
	}
}

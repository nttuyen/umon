package com.nttuyen.android.umon.core.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class JsonGenericConverter extends JsonConverter {

    static {
        JsonConvertHelper.register(JsonGenericConverter.class.getName(), new JsonGenericConverter());
    }

	public <T> T inject(JSONObject json, T target) throws JSONException {
		try {
			Class<?> type = target.getClass();

			Field[] fields = type.getDeclaredFields();
			for(Field field : fields) {
				Class fieldType = field.getType();
				String jsonField = field.getName();

				Json annotation = field.getAnnotation(Json.class);
				if(annotation != null) {
					jsonField = annotation.name();
				}
				if(json.isNull(jsonField)) {
					continue;
				}

				Method setter = this.setterMethod(field, type);
				if(setter == null) {
					continue;
				}

				Object fieldValue = null;
				if(annotation!= null && annotation.isCollection()) {
					//TODO: only support for Collection: List, Set interface and concrete type;
					Collection collection = null;
					if(fieldType.equals(Collection.class) || fieldType.equals(List.class)) {
						collection = new ArrayList();
					} else if(fieldType.equals(Set.class)) {
						collection = new HashSet();
					} else {
						try {
							collection = (Collection)fieldType.newInstance();
						} catch (Exception ex) {
							continue;
						}
					}

					Class<?> typeInList = annotation.type();
					JSONArray jsonArray = json.getJSONArray(jsonField);
					this.inject(jsonArray, typeInList, collection);

					if(collection != null) {
						fieldValue = collection;
					}

				} else {
					if(isPrimaryType(fieldType)) {
						fieldValue = fieldType.cast(json.get(jsonField));
					} else {
						JSONObject jsonObject = json.getJSONObject(jsonField);
						fieldValue = fieldType.newInstance();
						this.inject(jsonObject, fieldValue);
					}
				}

				//setField(value, field, jsonField, json);
				if(fieldValue != null) {
					setter.invoke(target, fieldValue);
				}

			}

			return target;

		} catch (Exception e) {
			JSONException ex = new JSONException("exception");
			ex.initCause(e);
			throw ex;
		}
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
				.deleteCharAt(3);

		String methodName = builder.toString();
		try {
			Method method = type.getMethod(methodName, field.getType());
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
				.deleteCharAt(3);

		String methodName = builder.toString();
		Method[] methods = type.getMethods();
		for(Method m : methods) {
			if(m.getName().equals(methodName)) {
				return m;
			}
		}

		return null;
	}
}

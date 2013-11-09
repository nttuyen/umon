package com.nttuyen.android.umon.core.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * @author nttuyen266@gmail.com
 */
public class ReflectUtil {
	/**
	 * Return all field define in current class and supper class of this class
	 * @param type
	 * @return
	 */
	public static Set<Field> getAllField(Class type) {
		Set<Field> fields = new HashSet<Field>();

		Field[] fs;
		Class parent = type;
		while (parent != null && !parent.equals(Object.class)) {
			fs = parent.getDeclaredFields();
			for(Field f : fs) {
				fields.add(f);
			}
			parent = parent.getSuperclass();
		}

		return fields;
	}

	public static Method getterMethod(Field field, Class type) {
		if(field == null) return null;
		type = type != null ? type : field.getDeclaringClass();

		String fieldName = field.getName();
		String upper = fieldName.toUpperCase();
		StringBuilder builder = new StringBuilder();
		builder.append("get")
				.append(upper.charAt(0))
				.append(fieldName)
				.deleteCharAt(4);

		String methodName = builder.toString();

		try {
			Method method = type.getMethod(methodName);
			setAccessible(method);
			return method;
		} catch (NoSuchMethodException e) {}

		return null;
	}

	public static Method setterMethod(Field field, Class type) {
		if(field == null) return null;

		type = type != null ? type : field.getDeclaringClass();
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
			setAccessible(method);
			return method;
		} catch (Exception ex) {
			return null;
		}
	}

	public static Object getValueOfField(Field field, Object target) {
		if(target == null || field == null) return null;
		setAccessible(field);

		Class type = target.getClass();
		Method getter = getterMethod(field, type);

		try {
			Object value = getter != null ? getter.invoke(target) : field.get(target);
			return value;
		} catch (IllegalAccessException ex) {

		} catch (InvocationTargetException ex) {

		}
		return null;
	}

	public static void setValueOfField(Field field, Object value, Object target) {
		if(target == null || field == null) return;
		setAccessible(field);

		Method setter = setterMethod(field, target.getClass());

		try {
			if(setter != null) {
				setter.invoke(target, value);
			} else {
				field.set(target, value);
			}
		} catch (IllegalAccessException ex) {

		} catch (InvocationTargetException ex) {

		}
	}

	public static void setAccessible(Field field) {
		if(field != null) {
			field.setAccessible(true);
		}
	}
	public static void setAccessible(Method method) {
		if(method != null) {
			method.setAccessible(true);
		}
	}
}

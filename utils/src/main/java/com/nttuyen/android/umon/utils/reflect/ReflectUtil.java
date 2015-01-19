package com.nttuyen.android.umon.utils.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
	public static Field[] getAllFields(Class type) {
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

		return fields.toArray(new Field[fields.size()]);
	}
    public static Field[] getFieldsByAnnotation(Class type, Class<? extends Annotation> annotation) {
        Set<Field> fields = new HashSet<Field>();

        Field[] fs;
        Class parent = type;
        while (parent != null && !parent.equals(Object.class)) {
            fs = parent.getDeclaredFields();
            for(Field f : fs) {
                Annotation a = f.getAnnotation(annotation);
                if(a != null) {
                    fields.add(f);
                }
            }
            parent = parent.getSuperclass();
        }

        return fields.toArray(new Field[fields.size()]);
    }

    /*public static Field[] getAllFields(Class clazz) {
        if(clazz == null) {
            return new Field[0];
        }

        Set<Field> fields = new HashSet<Field>();

        Field[] fs;
        fs = clazz.getDeclaredFields();
        for(Field f : fs) {
            fields.add(f);
        }
        fs = clazz.getFields();
        for(Field f : fs) {
            fields.add(f);
        }

        return fields.toArray(new Field[fields.size()]);
    }*/

    /**
     * get All class by annotation
     * @param clazz
     * @param annotation
     * @return
     */
    public static Method[] getMethods(Class clazz, Class<? extends Annotation> annotation) {
        if(clazz == null || annotation == null) {
            return new Method[0];
        }
        final Set<Method> methods = new HashSet<Method>();
        Method[] ms;
        ms = clazz.getDeclaredMethods();
        for(Method m : ms) {
            Annotation a = m.getAnnotation(annotation);
            if(a != null) {
                methods.add(m);
            }
        }

        ms = clazz.getMethods();
        for(Method m : ms) {
            Annotation a = m.getAnnotation(annotation);
            if(a != null) {
                methods.add(m);
            }
        }

        return methods.toArray(new Method[methods.size()]);
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

    public static boolean isPrimary(Class c) {
        //TODO: @nttuyen update this method please
        return c.isPrimitive();
    }
}

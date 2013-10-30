package com.nttuyen.android.umon.core.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author nttuyen266@gmail.com
 */
public class JsonConvertHelper {
    private static final Map<String, JsonConverter> converters = new HashMap<String, JsonConverter>();

    static {
        try {
            Class.forName("com.nttuyen.android.base.json.JsonGenericConverter");
        } catch (ClassNotFoundException e) {}
    }

    public static void register(String name, JsonConverter converter) {
        converters.put(name, converter);
    }

	public static <T> T inject(JSONObject json, T target) throws Exception {
		Class<?> type = target.getClass();
		JsonConverter converter = converters.get(type.getName());
		if(converter == null) {
			converter = converters.get(JsonGenericConverter.class.getName());
		}
		if(converter == null)
			return target;

		converter.inject(json, target);
		return target;
	}
	public static <T> T convert(JSONObject json, Class<T> type) throws Exception {
		T target = type.newInstance();
		return inject(json, target);
	}
	public static <T> Collection<T> inject(JSONArray json, Class<T> type, Collection<T> collection) throws Exception {
		JsonConverter converter = converters.get(type.getName());
		if(converter == null) {
			converter = converters.get(JsonGenericConverter.class.getName());
		}
		if(converter == null)
			return collection;

		return converter.inject(json, type, collection);
	}
	public static <T> Collection<T> convert(JSONArray json, Class<T> type) throws Exception {
		Collection<T> collection = new ArrayList<T>();
		return inject(json, type, collection);
	}
}

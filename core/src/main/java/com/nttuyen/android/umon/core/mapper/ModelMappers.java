package com.nttuyen.android.umon.core.mapper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author nttuyen266@gmail.com
 */
public class ModelMappers {

	private static final Map<String, ModelMapper> mappers = new HashMap<String, ModelMapper>();

	static {
		JSONArrayMapper jsonArrayMapper = new JSONArrayMapper();
		JSONObjectMapper jsonObjectMapper = new JSONObjectMapper();
		jsonArrayMapper.setJsonObjectMapper(jsonObjectMapper);
		jsonObjectMapper.setJsonArrayMapper(jsonArrayMapper);

		register(JSONObject.class, null, jsonObjectMapper);
		register(JSONArray.class, null, jsonArrayMapper);
	}

	public static <Source, Target> void register(Class<Source> sourceClass, Class<Target> targetClass, ModelMapper<Source, Target> mapper) {
		if(mapper == null) {
			return;
		}
		String name1 = sourceClass != null ? sourceClass.toString() : "NULL";
		String name2 = targetClass != null ? targetClass.toString() : "NULL";

		String key = name1 + "__" + name2;
		mappers.put(key, mapper);
	}

	static <Source, Target> ModelMapper<Source, Target> getMapper(Class<Source> sourceClass, Class<Target> targetClass) {
		String name1 = sourceClass != null ? sourceClass.toString() : "NULL";
		String name2 = targetClass != null ? targetClass.toString() : "NULL";

		String sumKey = name1 + "__" + name2;
		if(mappers.containsKey(sumKey)) {
			return mappers.get(sumKey);
		}

		String sourceKey = name1 + "__" + "NULL";
		if(mappers.containsKey(sourceKey)) {
			return mappers.get(sourceKey);
		}

		String targetKey = "NULL" + "__" + name2;
		if(mappers.containsKey(targetKey)) {
			return mappers.get(targetKey);
		}

		return null;
	}

	public static <Source, Target> Target map(Source source, Target target) {
		if(source == null || target == null) {
			return null;
		}

		ModelMapper<Source, Target> mapper = getMapper((Class<Source>)source.getClass(), (Class<Target>)target.getClass());
		if(mapper != null) {
			return mapper.map(source, target);
		}

		return null;
	}
}

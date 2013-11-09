package com.nttuyen.android.umon.core.mapper;

import com.nttuyen.android.umon.core.json.Json;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author nttuyen266@gmail.com
 */
public class TestMapper {
	@Test
	public void testFromSimpleJSON() throws Exception {
		String jsonString = "{id: 10, name: \"name value\"}";
		JSONObject json = new JSONObject(jsonString);
		SimpleClass simple = new SimpleClass();
		HelpMapper.map(json, simple);

		Assert.assertNotNull(simple);
		Assert.assertEquals(10, simple.getId());
		Assert.assertEquals("name value", simple.getName());
	}

	@Test
	public void testIntegerJSONArray() throws Exception {
		String jsonString = "[1, 2, 3, 4, 5, 6, 7, 8, 9]";
		JSONArray json = new JSONArray(jsonString);
		int[] array = new int[0];

		array = HelpMapper.map(json, array);

		Assert.assertNotNull(array);
		Assert.assertEquals(9, array.length);
		Assert.assertArrayEquals(new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9}, array);
	}

	@Test
	public void testLongJSONArray() throws Exception {
		String jsonString = "[1, 2, 3, 4, 5, 6, 7, 8, 9]";
		JSONArray json = new JSONArray(jsonString);
		long[] array = new long[0];

		array = HelpMapper.map(json, array);

		Assert.assertNotNull(array);
		Assert.assertArrayEquals(new long[] {1, 2, 3, 4, 5, 6, 7, 8, 9}, array);
	}

	@Test
	public void testBooleanJSONArray() throws Exception {
		String jsonString = "[true, false, false, true]";
		JSONArray json = new JSONArray(jsonString);
		boolean[] array = new boolean[0];

		array = HelpMapper.map(json, array);

		Assert.assertNotNull(array);
		Assert.assertEquals(4, array.length);
		Assert.assertTrue(array[0]);
		Assert.assertFalse(array[1]);
		Assert.assertFalse(array[2]);
		Assert.assertTrue(array[3]);
	}

	@Test
	public void testDoubleJSONArray() throws Exception {
		String jsonString = "[1.1, 2.2, 3.3, 4.4, 5.5, 6.6, 7.7, 8.7, 9.9]";
		JSONArray json = new JSONArray(jsonString);
		double[] array = new double[0];

		array = HelpMapper.map(json, array);

		Assert.assertNotNull(array);
		Assert.assertArrayEquals(new double[] {1.1, 2.2, 3.3, 4.4, 5.5, 6.6, 7.7, 8.7, 9.9}, array, 0);
	}

	@Test
	public void testFloatJSONArray() throws Exception {
		String jsonString = "[1.1, 2.2, 3.3, 4.4, 5.5, 6.6, 7.7, 8.7, 9.9]";
		JSONArray json = new JSONArray(jsonString);
		float[] array = new float[0];

		array = HelpMapper.map(json, array);

		Assert.assertNotNull(array);
		Assert.assertArrayEquals(new float[] {1.1f, 2.2f, 3.3f, 4.4f, 5.5f, 6.6f, 7.7f, 8.7f, 9.9f}, array, 0);
	}

	@Test
	public void testStringJSONArray() throws Exception {
		String jsonString = "[\"value 1\", \"value 2\", \"value 3\", \"value 4\", \"value 5\"]";
		JSONArray json = new JSONArray(jsonString);
		String[] array = new String[0];
		array = HelpMapper.map(json, array);

		Assert.assertNotNull(array);
		Assert.assertArrayEquals(new String[]{"value 1", "value 2", "value 3", "value 4", "value 5"}, array);
	}

	@Test
	public void testObjectJSONArray() throws Exception {
		String jsonString = "[{id: 1, name: \"name 1\"}, {id: 2, name: \"name 2\"}, {id: 3, name: \"name 3\"}, {id: 4, name: \"name 4\"}]";
		JSONArray json = new JSONArray(jsonString);

		SimpleClass[] array = new SimpleClass[0];
		array = HelpMapper.map(json, array);

		Assert.assertNotNull(array);
		Assert.assertEquals(4, array.length);
		for(int i = 0; i < 4; i++) {
			Assert.assertEquals(i + 1, array[i].getId());
			Assert.assertEquals("name " + (i + 1), array[i].getName());
		}
	}

	@Test
	public void testJSONObjectJSONArray() throws Exception {
		String jsonString = "[{id: 1, name: \"name 1\"}, {id: 2, name: \"name 2\"}, {id: 3, name: \"name 3\"}, {id: 4, name: \"name 4\"}]";
		JSONArray json = new JSONArray(jsonString);

		JSONObject[] array = new JSONObject[0];
		array = HelpMapper.map(json, array);

		Assert.assertNotNull(array);
		Assert.assertEquals(4, array.length);
		for(int i = 0; i < 4; i++) {
			Assert.assertEquals(i + 1, array[i].getInt("id"));
			Assert.assertEquals("name " + (i + 1), array[i].getString("name"));
		}
	}

	@Test
	public void testJSONArrayJSonArray() throws Exception {
		String jsonString = "[[1, 2], [3, 4]]";
		JSONArray json = new JSONArray(jsonString);

		JSONArray[] array = new JSONArray[0];

		array = HelpMapper.map(json, array);

		Assert.assertNotNull(array);
		Assert.assertEquals(2, array.length);
		Assert.assertEquals(1, array[0].getInt(0));
		Assert.assertEquals(4, array[1].getInt(1));
	}

	@Test
	public void testComplexJSONObject() throws Exception {
		String jsonString = "{" +
				"id: 10, " +
				"name: \"name value\", " +
				"simple: {id: 10, name: \"name value\"}, " +
				"simples: [{id: 1, name: \"name 1\"}, {id: 2, name: \"name 2\"}, {id: 3, name: \"name 3\"}, {id: 4, name: \"name 4\"}]," +
				"collection: [{id: 1, name: \"name 1\"}, {id: 2, name: \"name 2\"}, {id: 3, name: \"name 3\"}, {id: 4, name: \"name 4\"}]}";

		JSONObject json = new JSONObject(jsonString);

		ComplexClass complex = new ComplexClass();
		HelpMapper.map(json, complex);

		Assert.assertNotNull(complex);
		Assert.assertEquals(10, complex.getId());
		Assert.assertEquals("name value", complex.getName());

		Assert.assertNotNull(complex.getSimple());
		SimpleClass simple = complex.getSimple();
		Assert.assertEquals(10, simple.getId());
		Assert.assertEquals("name value", simple.getName());

		Assert.assertNotNull(complex.getSimples());
		SimpleClass[] simples = complex.getSimples();
		Assert.assertEquals(4, simples.length);
		for(int i = 0; i < 4; i++) {
			Assert.assertEquals(i + 1, simples[i].getId());
			Assert.assertEquals("name " + (i + 1), simples[i].getName());
		}

		Assert.assertNotNull(complex.getCollection());
		Collection<SimpleClass> collection = complex.getCollection();
		Assert.assertEquals(4, collection.size());
		int i = 1;
		for(SimpleClass s : collection) {
			Assert.assertEquals(i, s.getId());
			Assert.assertEquals("name " + i, s.getName());
			i++;
		}
	}

	public static class SimpleClass {
		private int id;
		private String name;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	public static class ComplexClass {
		private long id;
		private String name;

		private SimpleClass simple;
		private SimpleClass[] simples;

		@Json(name = "collection", type = SimpleClass.class)
		private Collection<SimpleClass> collection = null;

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public SimpleClass getSimple() {
			return simple;
		}

		public void setSimple(SimpleClass simple) {
			this.simple = simple;
		}

		public SimpleClass[] getSimples() {
			return simples;
		}

		public void setSimples(SimpleClass[] simples) {
			this.simples = simples;
		}

		public Collection<SimpleClass> getCollection() {
			return collection;
		}

		public void setCollection(Collection<SimpleClass> collection) {
			this.collection = collection;
		}
	}
}

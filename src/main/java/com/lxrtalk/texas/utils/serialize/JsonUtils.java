package com.lxrtalk.texas.utils.serialize;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import com.lxrtalk.texas.entity.ListPo;

public class JsonUtils {

	private static final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
	private static final Gson gsonAll = new GsonBuilder().create();

	public static String toJson(Object src, Type typeOfSrc) {
		return gson.toJson(src, typeOfSrc);
	}
	public static String toJsonAll(Object src, Type typeOfSrc) {
		return gsonAll.toJson(src, typeOfSrc);
	}
	public static <T> T fromJson(String json, Class<T> classOfT) {
		return (T) gson.fromJson(json, classOfT);
	}

	public static <T> String toListJson(List<T> list) {
		ListPo<T> listpo = new ListPo<T>();
		listpo.setList(list);
		return gson.toJson(listpo, new TypeToken<ListPo<T>>() {
		}.getType());
	}

	public static <T> ListPo<T> fromListJson(String json, Type t) {
		return gson.fromJson(json, t);
	}

}

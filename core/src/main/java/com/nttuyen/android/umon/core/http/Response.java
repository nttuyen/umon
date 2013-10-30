package com.nttuyen.android.umon.core.http;

import org.apache.http.HttpResponse;

import java.io.InputStream;

/**
 * @author nttuyen266@gmail.com
 */
public abstract class Response<T> {
	public abstract void parse(InputStream input) throws Exception;

	public abstract T getResult();
}

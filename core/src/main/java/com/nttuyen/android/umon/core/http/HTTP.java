package com.nttuyen.android.umon.core.http;

import com.nttuyen.android.umon.core.Callback;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author nttuyen266@gmail.com
 */
public class HTTP {
	public static String NAME = "HTTP_REQUEST";

	public static String ERROR_EXCEPTION = "error_exception";
	public static String ERROR_RESPONSE = "error_response";

	public static String OPTION_URL = "url";
	public static String OPTION_METHOD = "method";
	public static String OPTION_DATA = "data";
	public static String OPTION_RESPONSE = "response";
	public static String OPTION_HEADERS = "headers";
	public static String OPTION_COMPLETED = "completed";
	public static String OPTION_ERROR = "error";
	public static String OPTION_SUCCESS = "success";

	static DefaultHttpClient client = null;
	static HttpClient createHttpClient() {
		if(client == null) {
			synchronized (HTTP.class) {
				if(client == null) {
					HttpParams params = new BasicHttpParams();
					HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
					HttpProtocolParams.setContentCharset(params, org.apache.http.protocol.HTTP.UTF_8);

					params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, new Integer(60000));
					params.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, new Integer(60000));
					params.setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true);

					client = new DefaultHttpClient(params);
				}
			}
		}
		return client;
	}

	public static <D> void execute(String url, D data, Map<String, Object> options) {
		if(options == null) {
			options = new HashMap<String, Object>();
		}
		options.put(OPTION_DATA, data);
		execute(url, options);
	}
	public static void execute(String url, Map<String, Object> options) {
		if(options == null) {
			options = new HashMap<String, Object>();
		}
		options.put(OPTION_URL, url);
		execute(options);
	}

	/**
	 * Option is Map that has: <br/>
	 * OPTION_URL => String
	 * OPTION_METHOD => String - http method
	 * OPTION_DATA => Object (any type of data)
	 * OPTION_RESPONSE => Response => response object
	 * OPTION_HEADERS => Map<String, String> - header
	 * OPTION_COMPLETED => Callback - callback of error
	 * OPTION_ERROR => Callback - callback when execute completed
	 * OPTION_SUCCESS => Callback - callback with response code is 200
	 * HTTP_RESPONSE_CODE => Callback - callback of response code
	 *
	 * All callback will execute with param: statusCode, statusString, message, response
	 * @param options
	 */
	public static void execute(Map<String, Object> options) {
		if(options == null
				|| !options.containsKey(OPTION_URL)
				|| !(options.get(OPTION_URL) instanceof String)) {
			//throw new IllegalArgumentException("Argument is not valid");
			//This mean: 412 Precondition Failed
			//Response will null
			handleCallback(412, "Precondition Failed", "options argument is not valid", null, options);
		}

		String url = (String)options.get(OPTION_URL);

		String method = "GET";
		if(options.containsKey(OPTION_METHOD) && options.get(OPTION_METHOD) instanceof String) {
			method = (String)options.get(OPTION_METHOD);
		}
		method = method.toUpperCase();

		HttpRequestBase request = null;
		try {
			if("GET".equals(method)) {
				request = createHttpGet(url, options);
			} else if("POST".equals(method)) {
				request = createHttpPost(url, options);
			} else {
				//This is mean not supported: 412 Precondition Failed
				handleCallback(412, "Precondition Failed", "Method " + method + "is not supported now", null, options);
			}
		} catch (Exception e) {
			handleCallback(412, "Precondition Failed", "Can not create HttpRequest because exception: " + e.getMessage(), null, options);
			return;
		}

		InputStream result = null;
		try {
			HttpClient client = createHttpClient();

			HttpResponse res = client.execute(request);

			StatusLine status = res.getStatusLine();
			int statusCode = status.getStatusCode();
			String statusString = status.getReasonPhrase();

			Response response = null;
			if(statusCode >= 200 && statusCode < 300) {
				HttpEntity entity = res.getEntity();
				result = entity.getContent();

				//Process response:
				if(options.containsKey(OPTION_RESPONSE) && options.get(OPTION_RESPONSE) instanceof Response) {
					response = (Response)options.get(OPTION_RESPONSE);
					if(response != null) {
						response.parse(result);
					}
				}
			}
			handleCallback(statusCode, statusString, "completed", response, options);
		} catch (Exception ex) {
			handleCallback(500, "Internal Server Error", "Unknown error when execute http", null, options);
		} finally {
			if(result != null) {
				try {
					result.close();
				} catch (IOException e) {}
			}
		}
	}

	private static void handleCallback(int status, String statusString, String message, Response response, Map<String, Object> options) {
		if(response == null) {
			if(options.containsKey(OPTION_RESPONSE) && options.get(OPTION_RESPONSE) instanceof Response) {
				response = (Response)options.get(OPTION_RESPONSE);
			}
		}
		//By status
		if(options.containsKey(String.valueOf(status)) && options.get(String.valueOf(status)) instanceof Callback) {
			Callback statusCallback = (Callback)options.get(String.valueOf(status));
			statusCallback.execute(status, statusString, message, response);
		}
		//Completed
		if(options.containsKey(OPTION_COMPLETED) && options.get(OPTION_COMPLETED) instanceof Callback) {
			Callback completedCallback = (Callback)options.get(OPTION_COMPLETED);
			completedCallback.execute(status, statusString, message, response);
		}

		//Error: 4xx and 5xx
		if(status >= 400 && status < 600) {
			if(options.containsKey(OPTION_ERROR) && options.get(OPTION_ERROR) instanceof Callback) {
				Callback errorCallback = (Callback)options.get(OPTION_ERROR);
				errorCallback.execute(status, statusString, message, response);
			}
		}

		//Success
		if(status >= 200 && status < 300) {
			if(options.containsKey(OPTION_SUCCESS) && options.get(OPTION_SUCCESS) instanceof Callback) {
				Callback success = (Callback)options.get(OPTION_SUCCESS);
				success.execute(status, statusString, message, response);
			}
		}
	}

	public static HttpGet createHttpGet(String url, Map<String, Object> options) {
		StringBuilder builder = new StringBuilder(url);

		if(options.containsKey(OPTION_DATA)) {
			Object data = options.get(OPTION_DATA);

			if(builder.indexOf("?") != -1) {
				builder.append('?');
			}

			if(data instanceof String) {
				String value = (String)data;
				builder.append("data=").append(value).append('&');
			} else if(data instanceof Map) {
				Map map = (Map)data;
				Set<Map.Entry> entries = map.entrySet();
				for(Map.Entry entry : entries) {
					if(entry.getKey() instanceof String) {
						builder.append(entry.getKey())
								.append('=')
								.append(String.valueOf(entry.getValue()))
								.append('&');
					}
				}
			}

			builder.deleteCharAt(builder.length() - 1);
		}

		HttpGet get = new HttpGet(builder.toString());
		return get;
	}

	public static HttpPost createHttpPost(String url, Map<String, Object> options) throws Exception {
		//Post form
		HttpEntity entity = null;
		if(options.containsKey(OPTION_DATA)) {
			Object data = options.get(OPTION_DATA);

			//Form data
			if(data instanceof Map) {
				Map map = (Map)data;
				List<NameValuePair> forms = new LinkedList<NameValuePair>();

				Set<Map.Entry> entries = map.entrySet();
				for(Map.Entry entry : entries) {
					if(entry.getKey() instanceof String) {
						NameValuePair pair = new BasicNameValuePair((String)entry.getKey(), String.valueOf(entry.getValue()));
						forms.add(pair);
					}
				}
				if(!forms.isEmpty()) {
					entity = new UrlEncodedFormEntity(forms, org.apache.http.protocol.HTTP.UTF_8);
				}
			} else if(data instanceof byte[]) {
				entity = new ByteArrayEntity((byte[])data);
			} else if(data instanceof HttpEntity) {
				entity = (HttpEntity)data;
			} else {
				//Call to string value
				entity = new StringEntity(String.valueOf(data), org.apache.http.protocol.HTTP.UTF_8);
			}
		}

		HttpPost post = new HttpPost(url);
		if(entity != null) {
			post.setEntity(entity);
		}

		return post;
	}
}

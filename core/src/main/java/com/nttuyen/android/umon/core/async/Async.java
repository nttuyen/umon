package com.nttuyen.android.umon.core.async;

import android.os.AsyncTask;
import android.util.Log;
import com.nttuyen.android.umon.core.Callback;
import com.nttuyen.android.umon.core.http.HTTP;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author nttuyen266@gmail.com
 */
public class Async {
	private static final String TAG = "umon-core" + Async.class.getName();

	public static final String OPTION_TASK_NAME = "staskName";
	public static final String OPTION_ON_PRE_EXECUTE = "onPreExecute";
	public static final String OPTION_ON_POST_EXECUTE = "onPostExecute";
	public static final String OPTION_ON_PROGRESS_UPDATE = "onProgressUpdate";
	public static final String OPTION_ON_CANCEL = "onCancel";

	private static final Map<String, Queue<Callback>> taskQueue = new ConcurrentHashMap<String, Queue<Callback>>();

	/**
	 * options:
	 * OPTION_TASK_NAME taskName - default HTTP.NAME
	 * OPTION_ON_PRE_EXECUTE Callback
	 * OPTION_ON_POST_EXECUTE Callback
	 * OPTION_ON_PROGRESS_UPDATE Callback
	 * OPTION_ON_CANCEL Callback
	 *
	 * All Callback when execute will is: execute(taskName, ...)
	 * @param options
	 */
	public static void http(final Map<String, Object> options) {
		if(options == null) {
			throw new IllegalArgumentException("Option must not be null");
		}

		String task = HTTP.NAME;
		Callback prec = null;
		Callback postc = null;
		Callback progressc = null;
		Callback cancelc = null;

		if(options.containsKey(OPTION_TASK_NAME) && options.get(OPTION_TASK_NAME) instanceof String) {
			task = (String)options.get(OPTION_TASK_NAME);
			options.remove(OPTION_TASK_NAME);
		}
		if(options.containsKey(OPTION_ON_PRE_EXECUTE) && options.get(OPTION_ON_PRE_EXECUTE) instanceof Callback) {
			prec = (Callback)options.get(OPTION_ON_PRE_EXECUTE);
			options.remove(OPTION_ON_PRE_EXECUTE);
		}
		if(options.containsKey(OPTION_ON_POST_EXECUTE) && options.get(OPTION_ON_POST_EXECUTE) instanceof Callback) {
			postc = (Callback)options.get(OPTION_ON_POST_EXECUTE);
			options.remove(OPTION_ON_POST_EXECUTE);
		}
		if(options.containsKey(OPTION_ON_PROGRESS_UPDATE) && options.get(OPTION_ON_PROGRESS_UPDATE) instanceof Callback) {
			progressc = (Callback)options.get(OPTION_ON_PROGRESS_UPDATE);
			options.remove(OPTION_ON_PROGRESS_UPDATE);
		}
		if(options.containsKey(OPTION_ON_CANCEL) && options.get(OPTION_ON_CANCEL) instanceof Callback) {
			cancelc = (Callback)options.get(OPTION_ON_CANCEL);
			options.remove(OPTION_ON_CANCEL);
		}

		final String name = task;
		final Callback pre = prec;
		final Callback post = postc;
		final Callback progress = progressc;
		final Callback cancel = cancelc;

		AsyncTask async = new AsyncTask() {
			@Override
			protected Object doInBackground(Object... objects) {
				HTTP.execute(options);
				return null;
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				if(pre != null) {
					pre.execute(name);
				}
			}

			@Override
			protected void onPostExecute(Object o) {
				super.onPostExecute(o);
				if(post != null) {
					post.execute(name);
				}
			}

			@Override
			protected void onProgressUpdate(Object... values) {
				super.onProgressUpdate(values);
				if(progress != null) {
					progress.execute(name);
				}
			}

			@Override
			protected void onCancelled() {
				super.onCancelled();
				if(cancel != null) {
					cancel.execute(name);
				}
			}
		};
		async.execute();
	}
	public static void http(String url, Map<String, Object> options) {
		if(options == null) {
			options = new HashMap<String, Object>();
		}
		options.put(HTTP.OPTION_URL, url);
		http(options);
	}

	public static void execute(final Callback callback) {
		AsyncTask task = new AsyncTask() {
			@Override
			protected Object doInBackground(Object... params) {
				callback.execute();
				return null;
			}
		};
		task.execute();
	}
	public static void execute(final Callback before, final Callback main, final Callback after) {
		AsyncTask task = new AsyncTask() {
			@Override
			protected Object doInBackground(Object... params) {
				main.execute();
				return null;
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				if(before != null) {
					before.execute();
				}
			}

			@Override
			protected void onPostExecute(Object o) {
				super.onPostExecute(o);
				if(after != null) {
					after.execute();
				}
			}
		};
		task.execute();
	}
	public static void execute(final String queue, final Callback callback) {
		if(callback == null) return;
		if(queue == null || queue.isEmpty()) {
			execute(callback);
			return;
		}

		try {
			Queue<Callback> q = taskQueue.get(queue);
			if(q == null) {
				q = new LinkedBlockingQueue<Callback>();
				if(q.offer(callback)) {
					//Init AsyncTask
					final Queue<Callback> tasks = q;
					AsyncTask task = new AsyncTask() {
						@Override
						protected Object doInBackground(Object... params) {
							Callback c = tasks.poll();
							while(c != null) {
								try {
									//TODO: what is best?
									//Thread.sleep(100);
								} catch (Exception ex) {}

								c.execute();
								c = tasks.poll();
							}
							taskQueue.remove(queue);
							return null;
						}
					};
					task.execute();
				}
			} else {
				q.offer(callback);
			}
		} catch (Throwable ex) {
			Log.e(TAG, "Exception", ex);
		}
	}
}

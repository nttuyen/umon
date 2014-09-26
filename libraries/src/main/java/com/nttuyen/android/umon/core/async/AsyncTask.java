package com.nttuyen.android.umon.core.async;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by tuyennt on 9/26/14.
 */
public abstract class AsyncTask<Param, Progress, Result> extends android.os.AsyncTask<Param, Progress, Result> {

    public static interface OnPreExecute {
        void execute();
    }
    public static interface OnPostExecute<R> {
        void execute(R result);
    }

    public static interface OnProgessUpdate<P> {
        void execute(P... progress);
    }

    private List<OnPreExecute> preExecutes = new LinkedList<OnPreExecute>();
    private List<OnPostExecute<Result>> postExecutes = new LinkedList<OnPostExecute<Result>>();
    private List<OnProgessUpdate<Progress>> progessUpdates = new LinkedList<OnProgessUpdate<Progress>>();

    @Override
    protected void onPreExecute() {
        for(OnPreExecute e : preExecutes) {
            e.execute();
        }
    }

    @Override
    protected void onPostExecute(Result result) {
        for(OnPostExecute<Result> e : postExecutes) {
            e.execute(result);
        }
    }

    @Override
    protected void onProgressUpdate(Progress... values) {
        for(OnProgessUpdate<Progress> e : progessUpdates) {
            e.execute(values);
        }
    }

    public AsyncTask onPreExecute(OnPreExecute e) {
        this.preExecutes.add(e);
        return this;
    }

    public AsyncTask onPostExecute(OnPostExecute<Result> e) {
        this.postExecutes.add(e);
        return this;
    }

    public AsyncTask onProgressUpdate(OnProgessUpdate<Progress> e) {
        this.progessUpdates.add(e);
        return this;
    }
}

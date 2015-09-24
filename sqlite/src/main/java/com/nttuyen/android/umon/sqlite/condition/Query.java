package com.nttuyen.android.umon.sqlite.condition;

/**
 * Created by nttuyen on 9/24/15.
 */
public class Query {
    public final String selection;
    private final String[] args;

    public Query(String selection, String[] args) {
        this.selection = selection;
        this.args = args;
    }

    public String[] getArgs() {
        String[] clone = new String[args.length];
        if(args.length > 0) {
            System.arraycopy(args, 0, clone, 0, args.length);
        }
        return clone;
    }
}

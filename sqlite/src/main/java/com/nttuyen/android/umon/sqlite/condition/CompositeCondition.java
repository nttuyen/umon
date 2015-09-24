package com.nttuyen.android.umon.sqlite.condition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by nttuyen on 9/24/15.
 */
public class CompositeCondition extends Condition {
    public static enum Type {
        AND, OR
    }

    public final Type type;
    public final List<Condition> conditions;

    public CompositeCondition(Type type) {
        this.type = type;
        this.conditions = new ArrayList<Condition>();
    }
    public CompositeCondition(Type type, Condition... cond) {
        this.type = type;
        this.conditions = new ArrayList<Condition>(Arrays.asList(cond));
    }

    @Override
    public synchronized Query toQuery() {
        String[] params = new String[0];
        StringBuilder sb = new StringBuilder();
        String[] subs = new String[conditions.size()];
        for(int i = 0; i < subs.length; i++) {
            if (sb.length() > 0) {
                sb.append(type == Type.AND ? " AND " : " OR ");
            }
            Query q = conditions.get(i).toQuery();
            sb.append("(").append(q.selection).append(')');
            params = concat(params, q.getArgs());
        }
        return new Query(sb.toString(), params);
    }

    private String[] concat(String[] p1, String[] p2) {
        String[] newParams = new String[p1.length + p2.length];
        if(p1.length > 0) {
            System.arraycopy(p1, 0, newParams, 0, p1.length);
        }
        if(p2.length > 0) {
            System.arraycopy(p2, 0, newParams, p1.length, p2.length);
        }
        return newParams;
    }
}

package com.nttuyen.android.umon.sqlite.condition;

/**
 * Created by nttuyen on 9/24/15.
 */
public class SingleCondition extends Condition {
    public static enum Type {
        EQ("# = ?"), LT("# < ?"), GT("# > ?"), LTE("# <= ?"), GTE("# >= ?");

        private final String expression;

        Type(String expression) {
            this.expression = expression;
        }

        String buildExpression(String field, String[] params) {
            String s = expression;
            s = s.replace("#", "{" + field + "}");
            return s;
        }
    }

    public final Type type;
    public final String field;
    private final String[] params;

    public SingleCondition(Type type, String field, String... params) {
        this.type = type;
        this.field = field;
        this.params = params;
    }

    protected String[] getParams() {
        String[] clone = new String[params.length];
        if (params.length > 0) {
            System.arraycopy(params, 0, clone, 0, params.length);
        }
        return clone;
    }

    @Override
    public Query toQuery() {
        return new Query(type.buildExpression(field, getParams()), getParams());
    }
}

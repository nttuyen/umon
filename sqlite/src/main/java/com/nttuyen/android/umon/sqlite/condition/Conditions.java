package com.nttuyen.android.umon.sqlite.condition;

/**
 * Created by nttuyen on 9/24/15.
 */
public class Conditions {
    public static Condition and(Condition... cond) {
        return new CompositeCondition(CompositeCondition.Type.AND, cond);
    }
    public static Condition or(Condition... cond) {
        return new CompositeCondition(CompositeCondition.Type.OR, cond);
    }
    public static Condition eq(String field, String value) {
        return new SingleCondition(SingleCondition.Type.EQ, field, value);
    }
    public static Condition lt(String field, String value) {
        return new SingleCondition(SingleCondition.Type.LT, field, value);
    }
    public static Condition gt(String field, String value) {
        return new SingleCondition(SingleCondition.Type.GT, field, value);
    }
    public static Condition lte(String field, String value) {
        return new SingleCondition(SingleCondition.Type.LTE, field, value);
    }
    public static Condition gte(String field, String value) {
        return new SingleCondition(SingleCondition.Type.GTE, field, value);
    }
}

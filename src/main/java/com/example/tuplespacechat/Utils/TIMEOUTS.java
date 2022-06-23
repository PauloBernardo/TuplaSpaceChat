package com.example.tuplespacechat.Utils;

public enum TIMEOUTS {
    PERMANENT(1800000), QUICK_CHECK(1000), CHECK(3000);

    final private int value;

    TIMEOUTS(int i) {
        value = i;
    }

    public int getValue() {
        return value;
    }
}

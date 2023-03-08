package com.del.cmc;

public class Mode {

    final private String key;
    final private String code;
    final private String name;

    public Mode(String key, String code, String name) {
        this.key = key;
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return name;
    }
}

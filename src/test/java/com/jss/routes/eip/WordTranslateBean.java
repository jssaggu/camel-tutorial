package com.jss.routes.eip;

import java.util.HashMap;
import java.util.Map;

public class WordTranslateBean {

    private Map<String, String> words = new HashMap<String, String>();

    public WordTranslateBean() {
        words.put("A", "Apple");
        words.put("B", "Bucket");
        words.put("C", "Cat");
    }

    public String translate(String key) {
        if (!words.containsKey(key)) {
            throw new IllegalArgumentException("Key not a known word " + key);
        }
        return key + "=" + words.get(key);
    }
}

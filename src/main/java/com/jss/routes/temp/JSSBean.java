package com.jss.routes.temp;

public class JSSBean {

    private final String word;

    public JSSBean(String word) {
        this.word = word;
    }

    public String say(String word2) {
        return "Hello " + word2 + " -- " + word;
    }
}

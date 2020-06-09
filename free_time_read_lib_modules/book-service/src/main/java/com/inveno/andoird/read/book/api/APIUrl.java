package com.inveno.andoird.read.book.api;

public class APIUrl {
    private static final String HOST_URL = "http://iai.inveno.com";

    public static String getUrl(String actionPath){
        return HOST_URL + actionPath;
    }
}

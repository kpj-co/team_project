package com.example.kpj.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("Hashtag")
public class Hashtag extends ParseObject {

    //limit to get hashtags
    private static final int MAX_NUMBER = 25;

    private static final String KEY_DESCRIPTION = "description";

    public String getDescription() {
       return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public static class Query extends ParseQuery<Hashtag> {
        public Query() {
            super(Hashtag.class);
        }

        public Query getTop() {
            setLimit(MAX_NUMBER);
            return this;
        }
    }
}

package com.example.kpj.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("University")
public class University  extends ParseObject {

    //Limit to get universities
    private static final int MAX_NUMBER = 25;

    //the column in the database that we need
    private static final String KEY_NAME = "name";

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }

    public static class Query extends ParseQuery<University> {
        public Query() {
            super(University.class);
        }

        public University.Query getTop() {
            //get the first x universities
            setLimit(MAX_NUMBER);

            return this;
        }
    }

}

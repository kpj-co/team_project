package com.example.kpj.model;


import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("Course")
public class Course extends ParseObject {

    //Limit to get courses
    private static final int MAX_NUMBER = 25;

    //columns in the database
    private static final String KEY_NAME = "name";

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }

    public static class Query extends ParseQuery<Course> {
        public Query() {
            super(Course.class);
        }

        public Query getTop() {
            //get the first x courses
            setLimit(MAX_NUMBER);

            return this;
        }
    }
}

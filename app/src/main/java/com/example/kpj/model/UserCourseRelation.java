package com.example.kpj.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("UserCourseRelation")
public class UserCourseRelation extends ParseObject {

    private static final int MAX_NUMBER = 25;
    private static final String KEY_USER = "user";
    private static final String KEY_COURSE = "course";

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public ParseObject getCourse() {
        return getParseObject(KEY_COURSE);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public void setCourse(ParseObject course) {
        put(KEY_COURSE, course);
    }

    public static class Query extends ParseQuery<UserCourseRelation> {
        public Query() {
            super(UserCourseRelation.class);
        }

        public Query getTop() {
            setLimit(MAX_NUMBER);
            return this;
        }

        //returns with the information of the user
        public Query withUser() {
            include(KEY_USER);
            return this;
        }

        //returns with the information of the course
        public Query withCourse() {
            include(KEY_COURSE);
            return this;
        }
    }
}

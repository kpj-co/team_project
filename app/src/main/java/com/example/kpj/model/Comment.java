package com.example.kpj.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("Comment")
public class Comment extends ParseObject {

    //Limit to get comments
    private static final int MAX_NUMBER = 25;

    private static final String KEY_USER = "user";
    private static final String KEY_POST = "post";
    private static final String KEY_DESCRIPTION = "description";

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public ParseObject getPost() {
        return getParseObject(KEY_POST);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public void setPost(ParseObject post) {
        put(KEY_POST, post);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    //A query just of the comment class
    public static class Query extends ParseQuery<Comment> {
        public Query() {
            super(Comment.class);
        }

        public Query getTop() {
            setLimit(MAX_NUMBER);
            return this;
        }

        //returns the comment with the information of the user
        public Query withUser() {
            include(KEY_USER);
            return this;
        }

        //returns the comment with the information of the post
        public Query withPost() {
            include(KEY_POST);
            return this;
        }

    }
}

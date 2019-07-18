package com.example.kpj.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("PostHashtagRelation")
public class PostHashtagRelation extends ParseObject {

    private static final int MAX_NUMBER = 25;

    private static final String KEY_POST = "post";
    private static final String KEY_HASHTAG = "hashtag";

    public ParseObject getPost() {
        return getParseObject(KEY_POST);
    }

    public ParseObject getHashtag() {
        return getParseObject(KEY_HASHTAG);
    }

    public void setPost(ParseObject post) {
        put(KEY_POST, post);
    }

    public void setHashtag(ParseObject hashtag) {
        put(KEY_HASHTAG, hashtag);
    }

    public static class Query extends ParseQuery<PostHashtagRelation> {
        public Query() {
            super(PostHashtagRelation.class);
        }

        public Query getTop() {
            setLimit(MAX_NUMBER);
            return this;
        }

        public Query withPost() {
            include(KEY_POST);
            return this;
        }

        public Query withHashtag() {
            include(KEY_HASHTAG);
            return this;
        }

    }
}

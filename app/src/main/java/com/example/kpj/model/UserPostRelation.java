package com.example.kpj.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("UserPostRelation")
public class UserPostRelation extends ParseObject {

    private static final String KEY_USER = "user";
    private static final String KEY_POST = "post";
    private static final String KEY_VOTE = "vote";

    public static final int DOWNVOTE = 2;
    public static final int UPVOTE = 1;
    public static final int NOVOTE = 0;

    public ParseUser getUser() { return getParseUser(KEY_USER); }

    public ParseObject getPost() { return getParseObject(KEY_POST); }

    public int getVote() { return getInt(KEY_VOTE); }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public void setPost(ParseObject post) {
        put(KEY_POST, post);
    }

    public void setVote(int vote) { put(KEY_POST, vote); }
}

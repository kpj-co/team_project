package com.example.kpj.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Post")
public class Post extends ParseObject {

    //names of the columns in the database
    private static final String KEY_USER = "user";
    private static final String KEY_MEDIA = "media";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_UPVOTES = "upvotes";
    private static final String KEY_DOWNVOTES = "downvotes";

    //getters
    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public ParseFile getMedia() {
        return getParseFile(KEY_MEDIA);
    }

    public String getTitle() {
        return getString(KEY_TITLE);
    }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public int getUpVotes() {
        return getInt(KEY_UPVOTES);
    }

    public int getDownVotes() {
        return getInt(KEY_DOWNVOTES);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public void setMedia(ParseFile media) {
        put(KEY_MEDIA, media);
    }

    public void setTitle(String title) {
        put(KEY_TITLE, title);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public void setUpVotes(int upvotes) {
        put(KEY_UPVOTES, upvotes);
    }

    public void setDownVotes(int downVotes) {
        put(KEY_DOWNVOTES, downVotes);
    }
}

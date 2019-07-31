package com.example.kpj.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;

@ParseClassName("Post")
public class Post extends ParseObject {

    //names of the columns in the database
    public static final String KEY_USER = "user";
    public static final String KEY_COURSE = "course";
    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_UPVOTES = "upvotes";
    public static final String KEY_DOWNVOTES = "downvotes";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_HASMEDIA = "hasMedia";

    public boolean isLiked = false;
    public boolean isDisliked = false;

    //Tradeoff space to be faster and deliver a better user experience in the chat
    private String title;
    private String description;
    private ParseFile parseFile;
    private ArrayList<String> hashtags = new ArrayList<>();

    //getters
    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public String getTitle() { return getString(KEY_TITLE); }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public int getUpVotes() {
        return getInt(KEY_UPVOTES);
    }

    public int getDownVotes() {
        return getInt(KEY_DOWNVOTES);
    }

    public boolean getHasMedia() {
        return getBoolean(KEY_HASMEDIA);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public void setHasMedia(Boolean hasMedia) {
        put(KEY_HASMEDIA, hasMedia);
    }

    public void setCourse(Course course) { put(KEY_COURSE, course); }

    public Course getCourse() {
        return (Course) getParseObject(KEY_COURSE);
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

    //Methods used to store extra data
    public void setTitleReference(String title) {
        this.title = title;
    }

    public void setDescriptionReference(String description) {
        this.description = description;
    }

    public void setMediaReference(ParseFile parseFile) {
        this.parseFile = parseFile;
    }

    public String getTitleReference() {
        return title;
    }

    public String getDescriptionReference() {
        return description;
    }

    public ParseFile getMediaReference() {
        return parseFile;
    }

    public ArrayList<String> getHashtags() {
        return hashtags;
    }

    public void addHashtag(String hashtag) {
        hashtags.add(hashtag);
    }

}

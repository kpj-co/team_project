package com.example.kpj.model;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
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
    public static final String KEY_IMAGEPREVIEW = "media";
    public static final String KEY_NUM_COMMENTS = "commentCount";

    //Limit to get posts
    private static final int MAX_NUMBER = 25;

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

    public ParseFile getMedia() {
        return getParseFile(KEY_IMAGEPREVIEW);
    }

    public Course getCourse() {
        return (Course) getParseObject(KEY_COURSE);
    }

    public int getNumComments() {return getInt(KEY_NUM_COMMENTS); }

    public void setMedia( ParseFile photo) { put(KEY_IMAGEPREVIEW, photo); }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public void setHasMedia(Boolean hasMedia) {
        put(KEY_HASMEDIA, hasMedia);
    }

    public void setCourse(Course course) { put(KEY_COURSE, course); }

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

    public void setCommentCount(int num) {
        put(KEY_NUM_COMMENTS, num);
    }

    public ArrayList<String> getHashtags() {
        return hashtags;
    }

    public void addHashtag(String hashtag) {
        hashtags.add(hashtag);
    }

    //A query just of the comment class
    public static class Query extends ParseQuery<Post> {
        public Query() {
            super(Post.class);
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
    }

}

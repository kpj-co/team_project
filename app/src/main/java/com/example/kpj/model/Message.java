package com.example.kpj.model;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.util.List;

@ParseClassName("Message")
public class Message extends ParseObject {

    public static final int MAX_NUMBER = 12;
    private static final String KEY_COURSE = "course";
    private static final String KEY_USER = "user";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_UNIVERSITY = "university";
    private static final String KEY_POST = "post";
    public static final String KEY_CREATED_AT = "createdAt";
    private String username;
    private ParseFile parseFileUserImage = null;

    //Some messages have a reference to the post, because they actually show a post in the chat
    private Post postReference = null;

    public Message() {

    }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public ParseObject getCourse() {
        return getParseObject(KEY_COURSE);
    }

    public void setDescription(String description) { put(KEY_DESCRIPTION, description); }

    public void setUniversity(University university) { put(KEY_UNIVERSITY, university); }

    public void setPost(Post post) { put(KEY_POST, post); }

    public ParseObject getPost() { return getParseObject(KEY_POST); }

    //TODO: Implement the method or something equivalent to it in the adapter
    public void setUserPhoto() throws ParseException{
        parseFileUserImage = this.getUser().fetchIfNeeded().getParseFile("photoImage");
    }

    public void setUserUsername() throws ParseException {
        username = this.getUser().fetchIfNeeded().getUsername();
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void setParseFileUserImage(ParseFile parseFileUserImage) {
        this.parseFileUserImage = parseFileUserImage;
    }

    public String getUsername() {
        return username;
    }

    public University getUniversity() {
        return (University)getParseObject(KEY_UNIVERSITY);
    }

    public ParseFile getParseFileUserImage() {
        return parseFileUserImage;
    }

    public Post getPostReference() {return postReference;}

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public void setCourse(ParseObject course) {
        put(KEY_COURSE, course);
    }

    public void setPostReference(Post post) {
        postReference = postReference;
    }

    public static class Query extends ParseQuery<Message> {
        public Query() {
            super(Message.class);
        }

        public Query getTop() {
            setLimit(MAX_NUMBER);
            return this;
        }

        public Query withUser() {
            include(KEY_USER);
            return this;
        }

        public Query withCourse() {
            include(KEY_COURSE);
            return this;
        }

        public Query withPost() {
            include(KEY_POST);
            return this;
        }
    }
}

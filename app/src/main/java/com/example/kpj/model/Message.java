package com.example.kpj.model;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

@ParseClassName("Message")
public class Message extends ParseObject {

    private static final int MAX_NUMBER = 25;

    private static final String KEY_COURSE = "course";
    private static final String KEY_USER = "user";
    private static final String KEY_DESCRIPTION = "description";
    private String username;
    private ParseFile parseFileUserImage = null;

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public ParseObject getCourse() {
        return getParseObject(KEY_COURSE);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    //TODO: Implement the method or something equivalent to it in the adapter
    public void setUserPhoto() throws ParseException{
/*            ParseQuery<ParseUser> query = ParseUser.getQuery();
            ParseUser user = this.getUser();
            Log.d("Username", user.fetchIfNeeded().getUsername());
            query.whereEqualTo("User", this.getUser());
            query.findInBackground(new FindCallback<ParseUser>() {

                @Override
                public void done(List<ParseUser> users, ParseException e) {
                    try {
                        parseFileUserImage = users.get(0).fetchIfNeeded().getParseFile("photoImage");
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }

                }
            });*/
        parseFileUserImage = this.getUser().fetchIfNeeded().getParseFile("photoImage");
    }

    public void setUserUsername() {
        username = this.getUser().getUsername();
    }

    public String getUsername() {
        return username;
    }

    public ParseFile getParseFileUserImage() {
        return parseFileUserImage;
    }


    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public void setCourse(ParseObject course) {
        put(KEY_COURSE, course);
    }

    public static class Query extends ParseQuery<Message> {
        public Query() {
            super(Message.class);
        }

        public Query getTop() {
            setLimit(MAX_NUMBER);
            return this;
        }

        public Query withuser() {
            include(KEY_USER);
            return this;
        }

        public Query withCourse() {
            include(KEY_COURSE);
            return this;
        }
    }
}

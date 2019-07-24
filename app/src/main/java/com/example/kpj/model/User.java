package com.example.kpj.model;

import android.widget.SearchView;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("_User")
public class User extends ParseUser {
    public static final String KEY_PROFILE = "photoImage";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_UNIVERSITY = "University";

    public String getUsername(){ return getString(KEY_USERNAME);}

    public void setUsername(String username){ put(KEY_USERNAME, username); }

    public ParseObject getUniversity(){ return getParseObject(KEY_UNIVERSITY);}

    public void setUniversity(ParseObject university){put(KEY_UNIVERSITY, university);}

    public static class Query extends ParseQuery<User>{

        public Query() { super(User.class);}

    }
}

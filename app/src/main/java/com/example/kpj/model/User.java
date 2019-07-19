package com.example.kpj.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseUser;

@ParseClassName("_User")
public class User extends ParseUser {

    public static final String KEY_PROFILE = "photoImage";

    public ParseFile getProfileImage() { return getParseFile(KEY_PROFILE);  }

    public void setProfileImage(ParseFile photo) { put (KEY_PROFILE, photo);
        ParseUser.getCurrentUser().saveInBackground();
    }
}

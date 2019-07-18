package com.example.kpj.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.File;

@ParseClassName("_User")
public class User extends ParseUser {

    private static final String KEY_PROFILE = "photoImage";

    public ParseFile getProfileImage() { return getParseFile(KEY_PROFILE); }

    public void setProfileImage(ParseFile photo) { put (KEY_PROFILE, photo); }
}

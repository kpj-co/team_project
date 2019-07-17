package com.example.kpj.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Hashtag")
public class Hashtag extends ParseObject {

    //limit to get hashtags
    private static final int MAX_NUMBER = 25;

    private static final String KEY_DESCRIPTION = "description";

    public String getDescription() {
       return getString(KEY_DESCRIPTION);
    }


}

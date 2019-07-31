package com.example.kpj.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("PostImageRelation")
public class PostImageRelation extends ParseObject {

    private static final String KEY_POST = "post";
    private static final String KEY_IMAGE = "image";

    public void setImage(ParseFile image) {
        put(KEY_IMAGE, image);
    }

    public void setPost(Post post) {
        put(KEY_POST, post);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public static class Query extends ParseQuery<PostImageRelation> {
        public Query() {
            super(PostImageRelation.class);
        }
    }

}

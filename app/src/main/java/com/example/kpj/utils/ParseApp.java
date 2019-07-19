package com.example.kpj.utils;

import android.app.Application;

import com.example.kpj.R;
import com.example.kpj.model.Comment;
import com.example.kpj.model.Course;
import com.example.kpj.model.Hashtag;
import com.example.kpj.model.Message;
import com.example.kpj.model.Post;
import com.example.kpj.model.PostHashtagRelation;
import com.example.kpj.model.UserCourseRelation;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //Inform about our custom classes
        ParseObject.registerSubclass(Comment.class);
        ParseObject.registerSubclass(Course.class);
        ParseObject.registerSubclass(Hashtag.class);
        ParseObject.registerSubclass(Message.class);
        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(PostHashtagRelation.class);
        ParseObject.registerSubclass(UserCourseRelation.class);

        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.app_id))
                .clientKey(getString(R.string.master_key))
                .server(getString(R.string.server_url))
                .build();

        Parse.initialize(configuration);
    }
}

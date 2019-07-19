package com.example.kpj;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.example.kpj.model.Post;

import java.util.List;

public class CourseFeedAdapter extends AppCompatActivity {

    private Context context;
    private List<Post> mPosts;

    public CourseFeedAdapter(Context context, List<Post> posts) {
        this.context = context;
        mPosts = posts;
    }

}

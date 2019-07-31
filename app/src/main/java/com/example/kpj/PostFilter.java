package com.example.kpj;

import android.widget.Filter;
import android.widget.Filterable;

import com.example.kpj.activities.ComposePostActivity;
import com.example.kpj.model.Post;
import com.example.kpj.model.PostHashtagRelation;
import com.example.kpj.utils.PostAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostFilter implements Filterable {
    private ArrayList<Post> postsList;
    private ArrayList<PostHashtagRelation> postHashtagRelations = new ArrayList<>();
    private ArrayList<Post> filteredPosts = new ArrayList<>();

    //The int is how many hashtags has a certain post
    private Map<Post, Integer> map = new HashMap<>();

    private PostAdapter adapter;

    public PostFilter(ArrayList<Post> posts, final PostAdapter adapter) {
        postsList = posts;
        this.adapter =adapter;

        for(final Post post : posts) {
            PostHashtagRelation.Query query = new PostHashtagRelation.Query();
            query.whereEqualTo(PostHashtagRelation.KEY_POST, post);
            query.findInBackground(new FindCallback<PostHashtagRelation>() {
                @Override
                public void done(List<PostHashtagRelation> objects, ParseException e) {
                    //Added to the map just when you find the relation, to avoid bugs in getFilter()
                    map.put(post, 0);

                    postHashtagRelations.addAll(objects);

                }
            });

        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<String> constraints = ComposePostActivity.returnHashtags(constraint.toString());
                if(constraint != null && constraint.length() != 0) {
                    for(PostHashtagRelation postHashtagRelation : postHashtagRelations) {
                        if(constraints.contains(postHashtagRelation.getHashtag())) {
                            Integer times = map.get((Post) postHashtagRelation.getPost());
                            times++;
                        }
                    }

                    for(Map.Entry element : map.entrySet()) {
                        //If the post has all the hashtags
                        if(element.getValue() == (Integer)constraints.size()) {
                            filteredPosts.add((Post)element.getKey());
                        }
                    }
                    results.values = filteredPosts;
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredPosts = (ArrayList<Post>) results.values;

                if(filteredPosts == null || constraint.equals("")) {
                    filteredPosts = postsList;
                }

                adapter.setList(filteredPosts);
                adapter.notifyDataSetChanged();
            }
        };
    }
}

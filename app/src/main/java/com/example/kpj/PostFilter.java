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
    private ArrayList<Post> filteredPosts;
    //The int is how many hashtags (typed by the user)has a certain post. The string is the post id
    private Map<String, Integer> map = new HashMap<>();
    //To retrieve posts by ID
    private Map<String, Post> postByID = new HashMap<>();
    private PostAdapter adapter;

    public PostFilter(ArrayList<Post> posts, final PostAdapter adapter) {
        postsList = posts;
        this.adapter =adapter;
        updateFilter(posts);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                filteredPosts = new ArrayList<>();
                FilterResults results = new FilterResults();
                ArrayList<String> constraints = ComposePostActivity.returnHashtags(constraint.toString());
                if(constraint != null && constraint.length() != 0) {
                    for(PostHashtagRelation postHashtagRelation : postHashtagRelations) {
                        if(hashtagIsEligable(constraints, postHashtagRelation.getHashtag())) {
                            Post tempPost = (Post) postHashtagRelation.getPost();
                            int times = map.get(tempPost.getObjectId());
                            times++;
                            String id = tempPost.getObjectId();
                            map.put(id, times);
                        }
                    }
                    for(Map.Entry element : map.entrySet()) {
                        //If the post has all the hashtags
                        if(element.getValue() == (Integer)constraints.size()) {
                            String postID = (String)element.getKey();
                            filteredPosts.add((Post)postByID.get(postID));
                        }
                        //Since the maps do not restart, you need to refresh the integer value
                        map.put((String)element.getKey(), 0);
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

    //Updates the map to filter correctly
    public void updateFilter(List<Post> posts) {
        for(final Post post : posts) {
            PostHashtagRelation.Query query = new PostHashtagRelation.Query();
            query.whereEqualTo(PostHashtagRelation.KEY_POST, post);
            query.withPost();
            query.findInBackground(new FindCallback<PostHashtagRelation>() {
                @Override
                public void done(List<PostHashtagRelation> objects, ParseException e) {
                    //Added to the map just when you find the relation, to avoid bugs in getFilter()
                    map.put(post.getObjectId(), 0);
                    postByID.put(post.getObjectId(), post);
                    postHashtagRelations.addAll(objects);
                }
            });
        }
    }

    public void clearFilter() {
        map.clear();
        postByID.clear();
        postHashtagRelations.clear();
    }

    /**
     * hashtagIsEligable
     *
     * Returns true if what the user typed is the beginning of the
     * String hashtag
     *
     * @param constraints the hashtags typed by the user
     * @param hashtag a specific hashtag that we are using to compare
     * @return wheter or not this hashtag matches the desires of the user
     */
    private boolean hashtagIsEligable(ArrayList<String> constraints, String hashtag) {
        boolean bJudge = false;
        for(String constraint : constraints) {
            if(hashtag.toLowerCase().startsWith(constraint.toLowerCase())) {
                bJudge = true;
                break;
            }
        }
        return bJudge;
    }
}

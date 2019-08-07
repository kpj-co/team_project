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

public class PostByHashtagFilter extends Filter {
    private ArrayList<Post> postsList;
    private ArrayList<PostHashtagRelation> postHashtagRelations = new ArrayList<>();
    private ArrayList<Post> filteredPosts = new ArrayList<>();
    //The int is how many hashtags (typed by the user)has a certain post. The string is the post id
    private Map<String, Integer> postCountById = new HashMap<>();
    //To retrieve posts by ID
    private Map<String, Post> postByID = new HashMap<>();
    private PostAdapter adapter;

    public PostByHashtagFilter(ArrayList<Post> posts, final PostAdapter adapter) {
        postsList = posts;
        this.adapter =adapter;
        updateFilter(posts);
    }



    //Updates the postCountById to filter correctly
    public void updateFilter(List<Post> posts) {
        for(final Post post : posts) {
            PostHashtagRelation.Query query = new PostHashtagRelation.Query();
            query.whereEqualTo(PostHashtagRelation.KEY_POST, post);
            query.withPost();
            query.findInBackground(new FindCallback<PostHashtagRelation>() {
                @Override
                public void done(List<PostHashtagRelation> objects, ParseException e) {
                    postByID.put(post.getObjectId(), post);
                    postHashtagRelations.addAll(objects);
                }
            });
        }
    }

    public void clearFilter() {
        postCountById.clear();
        postByID.clear();
        postHashtagRelations.clear();
    }

    /**
     * hashtagIsEligible
     *
     * Returns true if what the user typed is the beginning of the
     * String hashtag
     *
     * @param constraints the hashtags typed by the user
     * @param hashtag a specific hashtag that we are using to compare
     * @return wheter or not this hashtag matches the desires of the user
     */
    private boolean hashtagIsEligible(ArrayList<String> constraints, String hashtag) {
        for(String constraint : constraints) {
            if(hashtag.toLowerCase().startsWith(constraint.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        filteredPosts.clear();
        FilterResults results = new FilterResults();
        ArrayList<String> constraints = ComposePostActivity.returnHashtags(constraint.toString());
        if(constraint != null && constraint.length() != 0) {
            for(PostHashtagRelation postHashtagRelation : postHashtagRelations) {
                if(hashtagIsEligible(constraints, postHashtagRelation.getHashtag())) {
                    Post post = (Post) postHashtagRelation.getPost();
                    String id = post.getObjectId();
                    int times = postCountById.getOrDefault(id, 0);
                    times++;

                    postCountById.put(id, times);
                }
            }
            for(Map.Entry entries : postCountById.entrySet()) {
                //If the post has all the hashtags
                if(entries.getValue() == (Integer)constraints.size()) {
                    String postID = (String)entries.getKey();
                    filteredPosts.add((Post)postByID.get(postID));
                }
                //Since the maps do not restart, you need to refresh the integer value
                postCountById.put((String)entries.getKey(), 0);
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
}

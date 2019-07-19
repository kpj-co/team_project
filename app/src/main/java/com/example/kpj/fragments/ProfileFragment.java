package com.example.kpj.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.kpj.R;
import com.example.kpj.model.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;

public class ProfileFragment extends Fragment {

    private static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;
    private ImageView imageView;
    private TextView tvUsername;

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance(int page) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPage = getArguments().getInt(ARG_PAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        imageView = view.findViewById(R.id.ivProfilePic);
        tvUsername = view.findViewById(R.id.tvProfileUsername);
        //findUser(ParseUser.getCurrentUser());
        ParseUser user = ParseUser.getCurrentUser();
        tvUsername.setText(user.getUsername());
        Glide.with(getContext())
                .load(user.getParseFile(User.KEY_PROFILE).getUrl())
                .into(imageView);
        return view;

    }
//
//    private void findUser(ParseUser user) {
//        final User.Query query = new User.Query();
//        query.whereEqualTo("username", user.getUsername());
//        query.findInBackground(new FindCallback<User>() {
//            @Override
//            public void done(List<User> objects, ParseException e) {
//                if(e == null){
//                    for(int i = 0; i < objects.size(); i++){
//                       Glide.with(getContext()).load(objects.get(i).getProfileImage()).into(imageView);
//                       tvUsername.setText(objects.get(i).getUsername());
//                    }
//                }
//            }
//        });
//    }
}


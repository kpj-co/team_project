package com.example.kpj.fragments;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.request.RequestOptions;
import com.example.kpj.activities.LoginActivity;
import com.example.kpj.R;
import com.example.kpj.model.ImagePreview;
import com.example.kpj.model.User;
import com.parse.ParseUser;


public class ProfileFragment extends Fragment {

    private static final String ARG_PAGE = "ARG_PAGE";

    private ImageView imageView;
    private TextView tvUsername;
    private Button btnLogOut;

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
            int mPage = getArguments().getInt(ARG_PAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        findViewsById(view);
        setBtnLogOut();
        ParseUser user = ParseUser.getCurrentUser();
        setUserProfileImage(user);
        return view;

    }

    private void setUserProfileImage(ParseUser user) {
        if (user.getParseFile(User.KEY_PROFILE) != null) {
            ImagePreview profilepic = new ImagePreview(user.getParseFile(User.KEY_PROFILE));
            profilepic.loadImage(getContext(), imageView, new RequestOptions().centerCrop());
        }
    }

    private void findViewsById(View view) {
        imageView = view.findViewById(R.id.ivProfilePic);
        tvUsername = view.findViewById(R.id.tvProfileUsername);
        btnLogOut = view.findViewById(R.id.bLogout);
    }

    public void setBtnLogOut() {
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                goToMainActivity();
            }
        });
    }

    private void goToMainActivity() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
    }
}


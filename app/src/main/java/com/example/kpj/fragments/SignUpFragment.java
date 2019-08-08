package com.example.kpj.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.kpj.CameraLauncher;
import com.example.kpj.R;
import com.example.kpj.model.User;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.io.File;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class SignUpFragment extends Fragment {

    private static final String ARG_PAGE = "ARG_PAGE";

    public EditText etNewEmail;
    public EditText etNewUsername;
    public EditText etNewPassword;
    public Button btnNewSignup;
    public TextView tvTakeProfilePic;
    public ImageView ivNewProfilePic;
    private SignUpFragmentListener callback;

    private final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public File photoFile;

    private String email;
    private String username;
    private String password;
    private String photo;
    private Boolean taken;

    public void setSignUpData(SignUpFragmentListener callback) {
        this.callback = callback;
    }

    public interface SignUpFragmentListener {
        void onSignUpSet(String username, String email, String password);
    }

    public static SignUpFragment newInstance(int page) {
        SignUpFragment fragment = new SignUpFragment();
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
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        initializeViews(view);
        setSignUpButtonListener();
        setCameraListener();
        return view;
    }

    public void initializeViews(View view) {
        etNewEmail = view.findViewById(R.id.etNewEmail);
        etNewPassword = view.findViewById(R.id.etNewPassword);
        etNewUsername = view.findViewById(R.id.etNewUser);
        btnNewSignup = view.findViewById(R.id.btnNewSignup);
        tvTakeProfilePic = view.findViewById(R.id.tvTakeProfilePic);
        ivNewProfilePic = view.findViewById(R.id.ivNewProfilePic);
    }

    public void getStringsFromEditTexts() {
        email = etNewEmail.getText().toString();
        username = etNewUsername.getText().toString();
        password = etNewPassword.getText().toString();
    }

    private void setSignUpButtonListener() {
        btnNewSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taken = false;
                getStringsFromEditTexts();
                setUpSharedPref();
                checkIfUserExists(username);
                }
        });
    }

    public void setCameraListener() {
        tvTakeProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLaunchCamera();
            }
        });
    }

    public void onLaunchCamera() {
        CameraLauncher cameraLauncher = new CameraLauncher(getActivity(), new CameraLauncher.Callback() {
            @Override
            public void startActivityForResult(Intent intent) {
                SignUpFragment.this.startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });
        cameraLauncher.requestCameraPermission();
        photoFile = cameraLauncher.onLaunchCamera();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                displayFile(photoFile.getAbsoluteFile());
                Toast.makeText(getContext(), "Profile Picture Set", Toast.LENGTH_LONG).show();
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void displayFile(File file) {
        Glide.with(getContext())
                .load(file)
                //crop photo to size of thing
                .apply(new RequestOptions().override(200, 200).centerCrop())
                .into(ivNewProfilePic);
    }

    private void setUpSharedPref() {
        if (photoFile != null) {
            photo = photoFile.toString();
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = prefs.edit();

        if (photoFile != null) {
            editor.putString("photo", photo);
        }
        editor.apply();
    }

   private void checkIfUserExists(final String username){
        User.Query userQuery = new User.Query();
        userQuery.whereEqualTo("username", username);
        userQuery.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> objects, ParseException e) {
                if(e == null){
                    if(objects.size() != 0){
                         taken = true;
                        Toast.makeText(getContext(), "Username is taken", Toast.LENGTH_LONG).show();
                    }
                    }
                boolean isTaken = taken;
                if(isTaken){
                    return;
                }
                else{
                    callback.onSignUpSet(username, email, password);
                    goToUniversityFragment();
                }
                }
        });
   }

    public void goToUniversityFragment() {
        Fragment fragment = new UniversityFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
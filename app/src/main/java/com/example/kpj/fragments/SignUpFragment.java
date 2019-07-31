package com.example.kpj.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
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
import java.io.File;
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
    private final static int CAMERA_PERMISSION_CODE = 1;
    public File photoFile;

    private String email;
    private String username;
    private String password;
    private String photo;

    public void setSignUpData(SignUpFragmentListener callback){
        this.callback = callback;
    }

    public interface SignUpFragmentListener{
        void onSignUpSet(String username, String password, String email);
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
                getStringsFromEditTexts();
                setUpSharedPref(photo);
                callback.onSignUpSet(username, password, email);
                goToUniversityFragment();
            }
        });
    }

    public void setCameraListener() {
        tvTakeProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCameraPermission();
            }
        });
    }

    private void requestCameraPermission() {
        try {
            if (ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]
                                {Manifest.permission.CAMERA, Manifest.permission.CAMERA},
                        CAMERA_PERMISSION_CODE);
            } else {
                onLaunchCamera();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onLaunchCamera() {
        CameraLauncher cameraLauncher = new CameraLauncher(getActivity(), new CameraLauncher.Callback() {
            @Override
            public void startActivityForResult(Intent intent) {
                SignUpFragment.this.startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });
        photoFile = cameraLauncher.onLaunchCamera();
        photo = photoFile.toString();
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

    private void setUpSharedPref(String photo){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("photo",photo);
        editor.apply();
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

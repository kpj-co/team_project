package com.example.kpj;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignupActivity extends AppCompatActivity {

    public EditText etNewEmail;
    public EditText etNewUsername;
    public EditText etNewPassword;
    public Button btnNewSignup;

    private String email;
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initializeViews();
        setSignUpButtonListener();
    }

    public void initializeViews() {
        etNewEmail = findViewById(R.id.etNewEmail);
        etNewPassword = findViewById(R.id.etNewPassword);
        etNewUsername = findViewById(R.id.etNewUser);
        btnNewSignup = findViewById(R.id.btnNewSignup);
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
                signUpUser(email, username, password);
            }
        });
    }

    public void signUpUser(String email, String username, String password) {
        ParseUser user = new ParseUser();
        // send data to parse for new user
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(password);
        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("SignUpActivity", "login Success!");
                    Toast.makeText(getApplicationContext(),"Welcome to Instagram!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Log.e("SignUpActivity", "Login Failed");
                    e.printStackTrace();
                }
            }
        });
    }



}

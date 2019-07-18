package com.example.kpj;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {


    //private ArrayList<String> pervUserInfo;

    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginButton;
    private TextView signupButton;

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            goToMainActivity();
        } else {

            usernameInput = findViewById(R.id.etUsername);
            passwordInput = findViewById(R.id.etPassword);
            loginButton = findViewById(R.id.btnLogin);
            signupButton = findViewById(R.id.tvSignup);

            username = usernameInput.getText().toString();

            //set click listener for login
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String username = usernameInput.getText().toString();
                    final String password = passwordInput.getText().toString();
                    login(username, password);
                }
            });

            //set click listener for sign up button
            signupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToSignupActivity();
                }
            });
        }
    }

    private void login(String username, String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    Log.d("LoginActivity", "login Success!");
                    Toast.makeText(getApplicationContext(),"Login Success",Toast.LENGTH_SHORT).show();
                    goToMainActivity();
                } else {
                    //TODO : Write to User that password and login do not match
                    Log.e("LoginActivity", "Login Failed");
                    e.printStackTrace();
                }
            }
        });
    }

    public void goToSignupActivity() {
        // Create the ParseUser
        ParseUser user = new ParseUser();
        // Create intent to SignUpActivity
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
    }

    //send user to home activity
    public void goToMainActivity() {
        Intent in = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(in);
        finish();
    }
}

package com.example.kpj.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kpj.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginButton;
    private TextView signUpButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            //goToMainActivity();
            goToCourseList();
        } else {
            usernameInput = findViewById(R.id.etUsername);
            passwordInput = findViewById(R.id.etPassword);
            loginButton = findViewById(R.id.btnLogin);
            signUpButton = findViewById(R.id.tvSignup);
            setLoginButtonListener();
            setSignUpButtonListener();
        }
    }

    private void setSignUpButtonListener() {
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSignUpActivity();
            }
        });
    }

    private void setLoginButtonListener() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            final String username = usernameInput.getText().toString();
            final String password = passwordInput.getText().toString();
            login(username, password);
            }
        });
    }

    private void login(String username, String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    Log.d("LoginActivity", "login Success!");
                    Toast.makeText(getApplicationContext(),"Login Success",Toast.LENGTH_SHORT).show();
                    //goToMainActivity();
                    goToCourseList();
                } else {
                    Toast.makeText(getApplicationContext(),"Username or Password is wrong",Toast.LENGTH_SHORT).show();
                    Log.e("LoginActivity", "Login Failed");
                    e.printStackTrace();
                }
            }
        });
    }

    public void goToSignUpActivity() {
        Intent intent = new Intent(LoginActivity.this, SignupFlowActivity.class);
        startActivity(intent);
    }

    private void goToCourseList() {
        Intent intent = new Intent(LoginActivity.this, CourseListActivity.class);
        startActivity(intent);
    }

}

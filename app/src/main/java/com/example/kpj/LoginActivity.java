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

    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginButton;
    private TextView signupButton;

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

            setLoginButtonListener();
            setSignupButtonListener();
        }
    }

    private void setSignupButtonListener() {
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSignupActivity();
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
                    goToMainActivity();
                } else {
                    Toast.makeText(getApplicationContext(),"Username or Password is wrong",Toast.LENGTH_SHORT).show();
                    Log.e("LoginActivity", "Login Failed");
                    e.printStackTrace();
                }
            }
        });
    }

    public void goToSignupActivity() {
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
package com.postace.instagramdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    RelativeLayout revlayout;
    ImageView logo;
    EditText usernameIn;
    EditText passwordIn;
    Button btnLogin;
    Button btnSignUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        revlayout = (RelativeLayout) findViewById(R.id.rev_layout);
        logo = (ImageView) findViewById(R.id.imageLogo);
        usernameIn = (EditText) findViewById(R.id.usernameIn);
        passwordIn = (EditText) findViewById(R.id.passwordIn);
        btnLogin = (Button) findViewById(R.id.btnLogIn);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        // set listenter for hide input keyboard
        revlayout.setOnClickListener(this);
        logo.setOnClickListener(this);
        // set Click listener for our button
        btnLogin.setOnClickListener(this);              // action login
        btnSignUp.setOnClickListener(this);             // action sign-up
        // if user already logged before -> move to list user activity
        if (ParseUser.getCurrentUser() != null) {
            showUserList();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogIn:
                logIn();
                break;
            case R.id.btnSignUp:
                signUp();
                break;
            case R.id.rev_layout:
            case R.id.imageLogo:
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                break;
        }
    }

    // sign up user
    public void signUp() {
        ParseUser user = new ParseUser();
        user.setUsername(usernameIn.getText().toString());
        user.setPassword(passwordIn.getText().toString());
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {            // sign up success
                    showUserList();         // move to list user activity
                } else {
                    showError(e);
                }
            }
        });
    }

    // login user
    public void logIn() {
        ParseUser.logInInBackground(usernameIn.getText().toString(),
            passwordIn.getText().toString(),
            new LogInCallback() {
                @Override
                public void done(ParseUser parseUser, ParseException e) {
                    if (e == null) {        // login success
                        showUserList();     // move to list user activity
                    } else {
                        showError(e);
                    }
                }
            });
    }

    // displaying the ParseException error
    public void showError(ParseException e) {
        switch (e.getCode()) {
            case ParseException.USERNAME_MISSING:
                showToast("Please enter username");
                break;
            case ParseException.PASSWORD_MISSING:
                showToast("Please enter password");
                break;
            case ParseException.USERNAME_TAKEN:
                showToast("Username already exist");
                break;
            case ParseException.VALIDATION_ERROR:
                showToast("Your username or password is incorrect");
                break;
        }
        e.printStackTrace();
    }

    // displaying the Toast message
    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    // showing list of user
    public void showUserList() {
        Intent i = new Intent(MainActivity.this, UserList.class);
        i.putExtra("username", ParseUser.getCurrentUser().getUsername());
        startActivity(i);
    }

}

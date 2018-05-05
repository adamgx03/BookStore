package com.abooth.bookstore;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity{

    private EditText usernameET;
    private EditText passwordET;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startup_login);

        //ref to edit texts
        usernameET = (EditText) findViewById(R.id.userNameEditText);
        passwordET = (EditText) findViewById(R.id.passwordEditText);
    }

    public void startActivityMain (View view){
        String username = usernameET.getText().toString();
        String password = passwordET.getText().toString();
        if (username.equals("username") && password.equals("password")) {
            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(mainIntent);
        } else {
            Toast.makeText(this, "Username or Password is incorrect.", Toast.LENGTH_SHORT).show();
        }
    }
}

package com.example.appinsta;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.appinsta.service.InstagramService;

import dev.niekirk.com.instagram4android.requests.payload.InstagramLoginResult;

public class LoginWithFacebook extends AppCompatActivity {
    InstagramService service = InstagramService.getInstance();
    public EditText etName, etPassword;
    public Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_with_facebook);
        etName = findViewById(R.id.edtEmailFacebook);
        etPassword = findViewById(R.id.edtPasswordFacebook);
        btnLogin = findViewById(R.id.btnLoginFacebook);
    }

}

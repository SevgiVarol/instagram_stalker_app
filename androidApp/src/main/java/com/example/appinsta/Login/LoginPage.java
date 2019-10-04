package com.example.appinsta.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Toast;


import com.example.appinsta.R;

import java.io.IOException;

import dev.niekirk.com.instagram4android.Instagram4Android;

public class LoginPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);


       // setContentView(R.layout.activity_login_page);

        Instagram4Android instagram = Instagram4Android.builder().username("nazliberil99").password("simsim").build();

        instagram.setup();
        try {
            instagram.login();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Toast.makeText(getApplicationContext(), String.valueOf(instagram.getUserId())
                ,Toast.LENGTH_LONG).show();
    }
}

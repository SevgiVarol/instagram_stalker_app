package com.example.appinsta;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appinsta.service.InstagramService;

import dev.niekirk.com.instagram4android.requests.payload.InstagramLoginResult;

public class LoginPage extends AppCompatActivity {
    InstagramService service = InstagramService.getInstance();
    public EditText etName, etPassword;
    public Button btnLogin;
    private long backPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        etName = findViewById(R.id.edtEmail);
        etPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);

    }

    //Login button onClick
    public void loginOnClick(View view) {
        btnLogin.setEnabled(false);

        String name = etName.getText().toString();
        String password = etPassword.getText().toString();
        try {
            InstagramLoginResult loginResult = service.login(name, password);
            if (loginResult.getStatus().equals("fail")) {
                throw new Exception();
            } else {
                Intent mainActivityIntent = new Intent(this, MainActivity.class);
                startActivity(mainActivityIntent);
                finish();
            }
        } catch (Exception e) {
            btnLogin.setEnabled(true);
            Toast.makeText(this, "Bilgileriniz eksik veya yanlış.", Toast.LENGTH_SHORT).show();
        }

    }

    //Back press trigger
    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finishAffinity();
        }
        backPressedTime = System.currentTimeMillis();
    }
}

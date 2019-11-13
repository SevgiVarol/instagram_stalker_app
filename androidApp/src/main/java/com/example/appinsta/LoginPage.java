package com.example.appinsta;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.appinsta.DataBase.LoggedUserDao;
import com.example.appinsta.DataBase.LoggedUserItem;
import com.example.appinsta.service.InstagramService;

import java.util.List;

import dev.niekirk.com.instagram4android.requests.payload.InstagramLoginResult;

public class LoginPage extends AppCompatActivity {
    InstagramService service = InstagramService.getInstance();
    public EditText etName, etPassword;
    public Button btnLogin;
    private long backPressedTime;
    LoggedUserDao loggedUserDao;
    LoggedUserItem loggedUserItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        AppDatabase database = Room.databaseBuilder(this, AppDatabase.class, "mydb")
                .allowMainThreadQueries()
                .build();
        loggedUserDao = database.loggedUserDao();
        loggedUserItem = new LoggedUserItem();
        List<LoggedUserItem> items = loggedUserDao.getLastUser();
        if (items.size() == 0){
        }else {
            try {
                if (service.getLoggedUser() != null){
                    Intent mainActivityIntent = new Intent(this, MainActivity.class);
                    startActivity(mainActivityIntent);
                    finish();
                }

            }catch (Exception e){
                try {
                    InstagramLoginResult loginResult = service.login(items.get(0).username, items.get(0).password);
                    if (loginResult.getStatus().equals("fail")) {
                        throw new Exception();
                    } else {
                        Intent mainActivityIntent = new Intent(this, MainActivity.class);
                        startActivity(mainActivityIntent);
                        finish();
                    }
                } catch (Exception exc) {}
            }

        }
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
                loggedUserItem.username = name;
                loggedUserItem.password = password;
                loggedUserDao.insertLastLogged(loggedUserItem);
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
        } else {
            Toast.makeText(this, "Çıkmak için tekrar geri tuşuna basın", Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
    }
}

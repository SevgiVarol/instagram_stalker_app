package com.example.appinsta;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.appinsta.database.InstaDatabase;
import com.example.appinsta.database.model.LoggedUserItem;
import com.example.appinsta.service.InstagramService;

import dev.niekirk.com.instagram4android.requests.payload.InstagramLoginResult;

public class LoginPage extends AppCompatActivity {
    InstagramService service = InstagramService.getInstance();
    public EditText etName, etPassword;
    private long backPressedTime;
    LoggedUserItem loggedUserItem, lastLoggedUser;
    InstaDatabase instaDatabase;
    Dialog loginDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        instaDatabase = InstaDatabase.getInstance(getApplicationContext());

        new loginWithLastUser().execute();
        etName = findViewById(R.id.edtEmail);
        etPassword = findViewById(R.id.edtPassword);

    }

    //Login button onClick
    public void loginOnClick(View view) {
        createDialogComponents();
        String name = etName.getText().toString();
        String password = etPassword.getText().toString();
        new saveUserAndLogin(name, password).execute();
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

    public void createDialogComponents() {
        loginDialog = new Dialog(this);
        loginDialog.setContentView(R.layout.waiting_for_loading);
        loginDialog.setCancelable(false);
        loginDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loginDialog.show();
        loginDialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                }
                return true;
            }
        });
    }

    private class loginWithLastUser extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute(){
            createDialogComponents();
        }

        @Override
        protected String doInBackground(String... strings) {
            lastLoggedUser = instaDatabase.loggedUserDao().getLastUser();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (lastLoggedUser != null) {
                //giriş fonksiyonunu çağır
                new login(lastLoggedUser).execute();
            } else {
                this.cancel(true);
            }
        }
    }

    private class saveUserAndLogin extends AsyncTask<String, String, String> {

        String name, password;

        public saveUserAndLogin(String name, String password) {
            this.name = name;
            this.password = password;
        }

        @Override
        protected void onPreExecute() {
            loggedUserItem = new LoggedUserItem();
            loggedUserItem.username = name;
            loggedUserItem.password = password;
        }

        @Override
        protected String doInBackground(String... strings) {
            lastLoggedUser = instaDatabase.loggedUserDao().getLastUser();
            if (lastLoggedUser != null) {
                instaDatabase.loggedUserDao().deleteLogged();
            }
            instaDatabase.loggedUserDao().insertLastLogged(loggedUserItem);
            return null;
        }

        protected void onPostExecute(String s) {
            new login(loggedUserItem).execute();
        }
    }

    private class login extends AsyncTask<String, String, String> {
        LoggedUserItem user;

        public login(LoggedUserItem user) {
            this.user = user;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                InstagramLoginResult loginResult = service.login(user.username, user.password);
                if (loginResult.getStatus().equals("fail")) {
                    throw new Exception();
                } else {
                    Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(mainActivityIntent);
                    finish();
                }
            } catch (Exception exc) {
                loginDialog.dismiss();
                runOnUiThread(new Runnable() {
                    public void run() {
                        final Toast toast = Toast.makeText(getApplicationContext(), "Bilgileriniz eksik veya yanlış.", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
            }
            return null;
        }
    }
}

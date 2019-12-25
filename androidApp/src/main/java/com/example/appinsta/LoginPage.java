package com.example.appinsta;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.example.appinsta.database.InstaDatabase;
import com.example.appinsta.database.model.LoggedUserItem;
import com.example.appinsta.service.InstagramService;
import com.example.appinsta.utils.Util;

import java.util.Locale;

import dev.niekirk.com.instagram4android.requests.payload.InstagramLoginResult;

public class LoginPage extends AppCompatActivity {
    InstagramService service = InstagramService.getInstance();
    LoggedUserItem loggedUserItem, lastLoggedUser;
    InstaDatabase instaDatabase;
    Dialog loginDialog;
    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.login_page);
        WebView loginWebView = findViewById(R.id.loginWebView);
        WebSettings webSettings = loginWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        loginWebView.addJavascriptInterface(new WebAppInterface(this), "Android");
        if (Locale.getDefault().getDisplayLanguage().equals("Türkçe")){
            actionBar.setTitle("Instagram ile giriş yap");
            loginWebView.loadUrl("file:///android_asset/dist/index_tr.html");
        }else {
            actionBar.setTitle("Login with Instagram");
            loginWebView.loadUrl("file:///android_asset/dist/index.html");
        }
        instaDatabase = InstaDatabase.getInstance(getApplicationContext());
        if (!Util.isNetworkAvailable(getApplicationContext())){
            Toast.makeText(getApplicationContext(),R.string.check_network_connection,Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    public class WebAppInterface {
        Context mContext;

        /** Instantiate the interface and set the context */
        WebAppInterface(Context c) {
            mContext = c;
        }

        /** Show a toast from the web page */
        @JavascriptInterface
        public void showToast(String name, String password) {
            createDialogComponents();
            new saveUserAndLogin(name, password).execute();
        }
    }

    public void createDialogComponents() {
        loginDialog = new Dialog(this);
        loginDialog.setContentView(R.layout.waiting_for_loading);
        loginDialog.setCancelable(false);
        loginDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loginDialog.show();
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
                return loginResult.getStatus();

            } catch (Exception exc) {
                exc.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String loginResult) {
            super.onPostExecute(loginResult);
            loginDialog.dismiss();
            if (loginResult == null || loginResult.equals("fail")) {
                final Toast toast = Toast.makeText(getApplicationContext(), R.string.login_failed, Toast.LENGTH_SHORT);
                toast.show();
            } else {
                Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainActivityIntent);
                finishAffinity();
            }
        }
    }
}

package com.basarsoft.instagramcatcher;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.basarsoft.instagramcatcher.database.InstaDatabase;
import com.basarsoft.instagramcatcher.database.model.LoggedUserItem;
import com.basarsoft.instagramcatcher.service.InstagramService;
import com.basarsoft.instagramcatcher.utils.Util;

import dev.niekirk.com.instagram4android.requests.payload.InstagramLoginResult;

public class StartPage extends AppCompatActivity {

    InstagramService service = InstagramService.getInstance();
    InstaDatabase instaDatabase;
    Dialog loginDialog;
    Button loginWithInstagram;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);
        instaDatabase = InstaDatabase.getInstance(getApplicationContext());
        loginWithInstagram = findViewById(R.id.loginWithInstagramButton);
        if (Util.isNetworkAvailable(getApplicationContext())){
            loginWithInstagram.setClickable(false);
            new loginWithLastUser().execute();
        } else {
            Toast.makeText(getApplicationContext(),R.string.check_network_connection,Toast.LENGTH_SHORT).show();
        }
        loginWithInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginPage = new Intent(getApplicationContext(),LoginPage.class);
                startActivity(loginPage);
            }
        });
    }
    private class loginWithLastUser extends AsyncTask<String, String, LoggedUserItem> {
        @Override
        protected LoggedUserItem doInBackground(String... strings) {
            try {
               return instaDatabase.loggedUserDao().getLastUser();
            }catch (Exception e){
                return null;
            }
        }

        @Override
        protected void onPostExecute(LoggedUserItem lastLoggedUser) {
            if (lastLoggedUser != null) {
                createDialogComponents();
                //giriş fonksiyonunu çağır
                new login(lastLoggedUser).execute();
            } else {
                loginWithInstagram.setClickable(true);
            }
        }
    }
    public void createDialogComponents() {
        loginDialog = new Dialog(this);
        loginDialog.setContentView(R.layout.waiting_for_loading);
        loginDialog.setCancelable(false);
        loginDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loginDialog.show();
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
                loginWithInstagram.setClickable(true);
                Intent loginActivity = new Intent(getApplicationContext(), LoginPage.class);
                startActivity(loginActivity);
            } else {
                loginWithInstagram.setClickable(true);
                Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainActivityIntent);
                finishAffinity();
            }
        }
    }
}

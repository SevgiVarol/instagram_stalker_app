package com.example.appinsta;

import android.app.Dialog;
import android.arch.persistence.room.Room;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.appinsta.database.LoggedUserDao;
import com.example.appinsta.database.LoggedUserItem;
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
    Dialog waitForLoadingDialog;
    List<LoggedUserItem> items;
    private loginWithLastUser lastUserTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        AppDatabaseForLogin database = Room.databaseBuilder(this, AppDatabaseForLogin.class, "logOfLogins").allowMainThreadQueries().build();
        loggedUserDao = database.loggedUserDao();
        loggedUserItem = new LoggedUserItem();
        items = loggedUserDao.getLastUser();

        if (items.size() != 0){
            createDialogComponents();
            try {
                if (service.getLoggedUser() != null){
                    waitForLoadingDialog.dismiss();
                    Intent mainActivityIntent = new Intent(this, MainActivity.class);
                    startActivity(mainActivityIntent);
                    finish();
                }

            }catch (Exception e){
                lastUserTask = (loginWithLastUser) new loginWithLastUser().execute();
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
                try {
                    loggedUserDao.deleteLogged(items.get(0));
                }catch (Exception e){}

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

    public void createDialogComponents(){
        waitForLoadingDialog = new Dialog(this);
        waitForLoadingDialog.setContentView(R.layout.waiting_for_loading);
        waitForLoadingDialog.setCanceledOnTouchOutside(false);
        waitForLoadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        waitForLoadingDialog.show();
        waitForLoadingDialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    lastUserTask.cancel(true);
                    waitForLoadingDialog.dismiss();
                }
                return true;
            }
        });
    }

    private class loginWithLastUser extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                InstagramLoginResult loginResult = service.login(items.get(0).username, items.get(0).password);
                if (loginResult.getStatus().equals("fail")) {
                    throw new Exception();
                } else {
                    waitForLoadingDialog.dismiss();
                    Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(mainActivityIntent);
                    finish();
                }
            } catch (Exception exc) {}
            return null;
        }
    }
}

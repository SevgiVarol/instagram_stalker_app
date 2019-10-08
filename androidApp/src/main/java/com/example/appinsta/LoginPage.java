package com.example.appinsta;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.appinsta.service.InstagramService;
import dev.niekirk.com.instagram4android.requests.payload.InstagramLoginResult;

public class LoginPage extends AppCompatActivity {
    InstagramService service=InstagramService.getInstance();
    public EditText edittext_ad,edittext_sifre;
    private long backPressedTime;
    public AnimationDrawable anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        edittext_ad=findViewById(R.id.edtEmail);
        edittext_sifre=findViewById(R.id.edtPassword);
        RelativeLayout container = (RelativeLayout) findViewById(R.id.container);
        anim = (AnimationDrawable) container.getBackground();
        anim.setEnterFadeDuration(100);
        anim.setExitFadeDuration(1000);
    }
    //Color animation
    @Override
    protected void onResume() {
        super.onResume();
        if (anim != null && !anim.isRunning())
            anim.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (anim != null && anim.isRunning())
            anim.stop();
    }

    //Login button onClick
    public void loginOnClick(View view) {
        String ad=edittext_ad.getText().toString();
        String sifre= edittext_sifre.getText().toString();
        try {
            InstagramLoginResult loginResult = service.login(ad, sifre);
            if(loginResult.getStatus().equals("fail")) {
                throw new Exception();
            }
            else finish();
        }
        catch (Exception e){
            Toast.makeText(this,"Bilgileriniz eksik veya yanlış.",Toast.LENGTH_SHORT).show();
        }

    }

    //Back press trigger
    @Override
    public void onBackPressed() {
        if (backPressedTime+2000 > System.currentTimeMillis()){
            super.onBackPressed();
            finishAffinity();
        }
        else {
            Toast.makeText(this,"Press back again to exit",Toast.LENGTH_SHORT).show();
        }
        backPressedTime= System.currentTimeMillis();
    }
}

package com.example.appinsta;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.FragmentManager;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

            MainFragment mainFragment= new MainFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.linearLayout, mainFragment).commit();

    }



}

  /*  private void test(User followers, Following following) {

        result2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              *//*  AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                        MainActivity.this);
                alertDialog.setTitle("Profile Info");

                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v = inflater.inflate(R.layout.user_info, null);
                alertDialog.setView(v);
              ImageView image=  (ImageView) v.findViewById(R.id.ivProfileImage);

                TextView tvName = (TextView) v.findViewById(R.id.tvUserName);

                TextView tvNoOfFollowing = (TextView) v
                        .findViewById(R.id.tvNoOfFollowing);


                    tvName.setText(userInfo.user.username);
                    tvNoOfFollowing.setText(userInfo.user.full_name);

                Picasso.with(getApplicationContext())
                        .load(userInfo.user.profile_pic_url)
                           // optional
                            // optional
                             // optional
                        .resize(78,70)
                        .into(image);





                alertDialog.create().show();
//                ivProfile.setImageResource(Integer.parseInt(userInfo.user.profile_pic_id));

                //final ArrayList<String> resultCompare2 ;

               // resultCompare2 = compare(followers.arrayListFollowing,following.arrayList);
                //resultlv.setAdapter(new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,resultCompare2));
            *//*
            }
        });


        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });


    }
}
*/
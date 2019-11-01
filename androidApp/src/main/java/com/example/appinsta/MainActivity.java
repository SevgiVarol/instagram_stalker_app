package com.example.appinsta;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.appinsta.service.InstagramService;

import java.io.Serializable;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dev.niekirk.com.instagram4android.requests.payload.InstagramUser;
import dev.niekirk.com.instagram4android.requests.payload.InstagramUserSummary;
import jp.wasabeef.glide.transformations.gpu.VignetteFilterTransformation;

import static com.example.appinsta.Compare.compare;


public class MainActivity extends AppCompatActivity implements Serializable {

    CustomView mutedStory, latestPhotoLikers, storyStalkers, photoStalkers, usersStalkers, usersStalking, userAction;
    List<InstagramUserSummary> mediaLikers, myFollowers, myFollowing, myStalkers, myStalking;

    ImageView profilPic, latestPhoto;
    TextView takipTv, takipciTv;

    RelativeLayout theLayout;
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar mProgress = null;
    Drawable drawable = null;
    InstagramUser user;
    InstagramService service = InstagramService.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponent();

        new setLoggedUserBasicInfoTask().execute();

        takipciTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), SearchActivity.class);
                i.putExtra("userList", (Serializable) myFollowers);
                startActivity(i);
            }
        });

        latestPhotoLikers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), SearchActivity.class);
                i.putExtra("userList", (Serializable) mediaLikers);
                startActivity(i);
            }
        });

        takipTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), SearchActivity.class);
                i.putExtra("userList", (Serializable) myFollowing);
                startActivity(i);
            }
        });

        usersStalkers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), SearchActivity.class);
                i.putExtra("userList", (Serializable) myStalkers);
                startActivity(i);
            }
        });
        usersStalking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), SearchActivity.class);
                i.putExtra("userList", (Serializable) myStalking);
                startActivity(i);
            }
        });

    }

    private class setLoggedUserBasicInfoTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {

            /*try {
                service.login("simge.keser", "Sim15290107.");
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            user = service.getLoggedUser();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            takipTv.setText(String.valueOf(user.following_count));
            takipciTv.setText(String.valueOf(user.follower_count));

            latestPhoto.setAlpha(0.3f);


            if (service.myMedia(0) != null) {
                Glide.with(getApplication()).load(service.getLoggedUserLastMediaUrl()).transform(new CenterCrop(), new VignetteFilterTransformation(new PointF(0.5f, 0.0f), new float[]{0f, 0f, 0f}, 0.5f, 0.9f)).into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            latestPhoto.setBackground(resource);
                        }
                    }
                });
            }

            Glide.with(getApplication()) //1
                    .load(user.getProfile_pic_url()).into(profilPic);
            mProgress.setVisibility(View.GONE);

            new getFollowingAndFollowersTask().execute();
        }

    }

    private class getFollowingAndFollowersTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {

            myFollowers = service.getMyFollowers();
            myFollowing = service.getMyFollowing();

            myStalking = compare(myFollowers, myFollowing);
            myStalkers = compare(myFollowing, myFollowers);

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (service.myMedia(0) != null) {
                mediaLikers = compare(myFollowers, service.getMyMediaLikers(service.myMedia(0).pk));
            }
            if (mediaLikers != null) {
                latestPhotoLikers.setNumberText(String.valueOf(mediaLikers.size()));
            } else latestPhotoLikers.setNumberText(String.valueOf(0));

            usersStalkers.setNumberText(String.valueOf(myStalkers.size()));
            usersStalking.setNumberText(String.valueOf(myStalking.size()));

        }
    }
    private void initComponent() {

        mProgress = findViewById(R.id.progress_bar);
        Resources res = getResources();
        drawable = res.getDrawable(R.drawable.circle_shape);

        profilPic = (CircleImageView) findViewById(R.id.userProfilPic);
        takipTv = (TextView) findViewById(R.id.takipTv);
        takipciTv = (TextView) findViewById(R.id.takipciTv);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        latestPhoto = (ImageView) findViewById(R.id.latestPhoto);

        theLayout = (RelativeLayout) findViewById(R.id.layoutLastestPhoto);

        mutedStory = (CustomView) findViewById(R.id.mutedStory);
        storyStalkers = (CustomView) findViewById(R.id.storyStalkers);
        photoStalkers = (CustomView) findViewById(R.id.photoStalkers);
        latestPhotoLikers = (CustomView) findViewById(R.id.latestPhotoLikers);
        userAction = (CustomView) findViewById(R.id.userAction);
        usersStalkers = (CustomView) findViewById(R.id.userStalkers);
        usersStalking = (CustomView) findViewById(R.id.userStalking);
    }
}

package com.example.appinsta;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.appinsta.enums.UserListTypes;
import com.example.appinsta.service.InstagramService;
import com.example.appinsta.userpage.UserProfileActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import dev.niekirk.com.instagram4android.requests.payload.InstagramFeedItem;
import dev.niekirk.com.instagram4android.requests.payload.InstagramUserSummary;

import static com.example.appinsta.Compare.compare;

public class SearchActivity<T> extends AppCompatActivity implements Serializable {

    private UserListAdapter adapter;
    EditText searchEditText;
    UserListTypes listType;
    long pk;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    InstagramService service = InstagramService.getInstance();
    public static List<InstagramUserSummary> myFollowers,myFollowing;
    List<InstagramUserSummary> usersFollowers,usersFollowings;
    ProgressDialog dialog;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");
        setContentView(R.layout.activity_search);
        searchEditText = (EditText) findViewById(R.id.editTextSearch);
        listType = (UserListTypes) getIntent().getSerializableExtra("listType");
        pk = getIntent().getLongExtra("userId",0);
        TextWatcher watcher = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                adapter.getFilter().filter(editable);
            }
        };

        searchEditText.addTextChangedListener(watcher);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(8);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        layoutManager = new LinearLayoutManager(getApplicationContext());

        new loadUsersListTask(listType).execute();

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    public void setRecyclerView(List<T> userList){
        if (userList != null) {
            adapter = new UserListAdapter(userList,getApplicationContext());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);

            adapter.setOnItemClickListener(new UserListAdapter.OnListener() {
                @Override
                public void onClick(int position) {
                    Intent i = new Intent(getApplicationContext(), UserProfileActivity.class);
                    i.putExtra("user", (Serializable) userList.get(position));
                    startActivity(i);
                }
            });
        }else {
            Toast.makeText(getApplicationContext(),R.string.wait_a_few_minute,Toast.LENGTH_LONG).show();
            finish();
        }
        dialog.cancel();
    }
    private class loadUsersListTask extends AsyncTask<String, String, List<T>> {
        UserListTypes listType;
        List<T> userList = new ArrayList<>();
        public loadUsersListTask(UserListTypes listType) {
            this.listType = listType;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog =new ProgressDialog(SearchActivity.this);
            switch (listType){
                case FOR_MY_FOLLOWERS:
                    actionBar.setSubtitle(R.string.follower);
                    break;

                case FOR_MY_FOLLOWINGS:
                    actionBar.setSubtitle(R.string.follow);
                    break;

                case FOR_MY_STALKERS:
                    actionBar.setSubtitle(R.string.who_not_follow_me_back);
                    break;

                case FOR_MY_STALKINGS:
                    actionBar.setSubtitle(R.string.who_i_am_not_follow_back);
                    break;

                case FOR_MY_LAST_PHOTO_LIKERS:
                    actionBar.setSubtitle(R.string.who_do_not_like_my_last_photo);
                    break;

                case FOR_USERS_FOLLOWERS:
                    actionBar.setSubtitle(R.string.follower);
                    dialog.setMessage(getApplicationContext().getResources().getString(R.string.user_follower_loading_message));
                    dialog.show();
                    break;

                case FOR_USERS_FOLLOWINGS:
                    actionBar.setSubtitle(R.string.follow);
                    dialog.setMessage(getApplicationContext().getResources().getString(R.string.user_following_loading_message));
                    dialog.show();
                    break;

                case FOR_USERS_STALKERS:
                    actionBar.setSubtitle(R.string.user_stalkers);
                    dialog.setMessage(getApplicationContext().getResources().getString(R.string.user_stalkers_loading_message));
                    dialog.show();
                    break;

                case FOR_USERS_STALKINGS:
                    actionBar.setSubtitle(R.string.user_stalkings);
                    dialog.setMessage(getApplicationContext().getResources().getString(R.string.user_stalkings_loading_message));
                    dialog.show();
                    break;
            }
        }

        @Override
        protected List<T> doInBackground(String... strings) {

            try {
                switch (listType){
                    case FOR_MY_FOLLOWERS:
                        userList = (List<T>) service.getMyFollowers();
                        break;

                    case FOR_MY_FOLLOWINGS:
                        userList = (List<T>) service.getMyFollowing();
                        break;

                    case FOR_MY_STALKERS:
                        if (myFollowing == null){
                            myFollowing = service.getMyFollowing();
                        }
                        if (myFollowers == null) {
                            myFollowers = service.getMyFollowers();
                        }
                        userList = (List<T>) compare(myFollowing, myFollowers);
                        break;

                    case FOR_MY_STALKINGS:
                        if (myFollowing == null){
                            myFollowing = service.getMyFollowing();
                        }
                        if (myFollowers == null) {
                            myFollowers = service.getMyFollowers();
                        }
                        userList = (List<T>) compare(myFollowers, myFollowing);
                        break;

                    case FOR_MY_LAST_PHOTO_LIKERS:
                        if (service.getLoggedUser().getMedia_count() > 0) {
                            if (myFollowers == null) {
                                myFollowers = service.getMyFollowers();
                            }
                            InstagramFeedItem firstItem = (InstagramFeedItem) service.getLoggedUserMedias(null).items.get(0);
                            userList = (List<T>) compare(myFollowers, service.getMediaLikers(firstItem.pk));
                        }
                        break;

                    case FOR_USERS_FOLLOWERS:
                        userList = (List<T>) service.getFollowers(pk);
                        break;

                    case FOR_USERS_FOLLOWINGS:
                        userList = (List<T>) service.getFollowing(pk);
                        break;

                    case FOR_USERS_STALKERS:
                        usersFollowers = service.getFollowers(pk);
                        usersFollowings = service.getFollowing(pk);
                        userList = (List<T>) compare(usersFollowers,usersFollowings);
                        break;

                    case FOR_USERS_STALKINGS:
                        usersFollowers = service.getFollowers(pk);
                        usersFollowings = service.getFollowing(pk);
                        userList = (List<T>) compare(usersFollowings,usersFollowers);
                        break;
                }
            }catch (Exception e){
                userList = null;
            }
            return userList;
        }

        @Override
        protected void onPostExecute(List<T> userList ) {
            super.onPostExecute(userList );
            actionBar.setTitle(String.valueOf(userList.size()) + " "+getApplicationContext().getResources().getString(R.string.users_count));
            setRecyclerView(userList );
        }
    }

}

package com.example.appinsta;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
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
import static com.example.appinsta.enums.UserListTypes.FOR_MY_FOLLOWERS;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        List<T> userList = new ArrayList<>();
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

        new getListTask().execute();

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
                    userList = (List<T>) Compare.compare(myFollowers, service.getMediaLikers(firstItem.pk));
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
        setRecyclerView(userList);
    }
    public void setRecyclerView(List<T> userList){
        if (userList != null) {
            if (userList.isEmpty()){
                Toast.makeText(getApplicationContext(),R.string.wait_a_few_minute,Toast.LENGTH_LONG).show();
            }
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
        }
    }
    private class getListTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            return null;
        }
    }

}

package com.example.appinsta;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.example.appinsta.enums.SearchActivityEnum;
import com.example.appinsta.models.DataWithOffsetIdModel;
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
    private List<T> userList=null;
    EditText searchEditText;
    SearchActivityEnum intentOption;
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

        searchEditText = (EditText) findViewById(R.id.editTextSearch);
        intentOption = (SearchActivityEnum) getIntent().getSerializableExtra("enum");
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


        switch (intentOption){
            case FOR_MY_FOLLOWERS:
                if (userList != null){userList = null;}
                userList = new ArrayList<>();
                userList = (List<T>) service.getMyFollowers();
                setRecyclerView(userList);


                break;

            case FOR_MY_FOLLOWINGS:
                if (userList != null){userList = null;}
                userList = new ArrayList<>();
                userList = (List<T>) service.getMyFollowing();
                setRecyclerView(userList);
                break;

            case FOR_MY_STALKERS:
                if (userList != null){userList = null;}
                userList = new ArrayList<>();
                if (myFollowing == null || myFollowers == null) {
                    myFollowers = service.getMyFollowers();
                    myFollowing = service.getMyFollowing();
                }
                userList = (List<T>) compare(myFollowing, myFollowers);
                setRecyclerView(userList);
                break;

            case FOR_MY_STALKINGS:
                if (userList != null){userList = null;}
                if (myFollowing == null || myFollowers == null) {
                    myFollowers = service.getMyFollowers();
                    myFollowing = service.getMyFollowing();
                }
                userList = (List<T>) compare(myFollowers, myFollowing);
                setRecyclerView(userList);
                break;

            case FOR_MY_LAST_PHOTO_LIKERS:
                if (service.getLoggedUser().getMedia_count() != 0) {
                    userList = new ArrayList<>();
                    if (myFollowers == null) {
                        myFollowers = service.getMyFollowers();
                    }
                    InstagramFeedItem firstItem = (InstagramFeedItem) service.getLoggedUserMedias(null).items.get(0);
                    userList = (List<T>) Compare.compare(myFollowers, service.getMediaLikers(firstItem.pk));
                    setRecyclerView(userList);
                }
                break;

            case FOR_USERS_FOLLOWERS:
                if (userList != null){userList = null;}
                userList = new ArrayList<>();
                userList = (List<T>) service.getFollowers(pk);
                setRecyclerView(userList);
                break;

            case FOR_USERS_FOLLOWINGS:
                if (userList != null){userList = null;}
                userList = (List<T>) service.getFollowing(pk);
                setRecyclerView(userList);
                break;

            case FOR_USERS_STALKERS:
                if (userList != null){userList = null;}
                usersFollowers = service.getFollowers(pk);
                usersFollowings = service.getFollowing(pk);
                userList = (List<T>) compare(usersFollowings,usersFollowers);
                setRecyclerView(userList);
                break;

            case FOR_USERS_STALKINGS:
                usersFollowers = service.getFollowers(pk);
                usersFollowings = service.getFollowing(pk);
                userList = (List<T>) compare(usersFollowers,usersFollowings);
                setRecyclerView(userList);
                break;
        }
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
        }
    }

}

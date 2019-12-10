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
import android.widget.Toast;

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
    int intentOption;
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
        intentOption = getIntent().getIntExtra("listType",0);
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
            case 0:
                userList = (List<T>) service.getMyFollowers();
                setRecyclerView(userList);
                break;

            case 1:
                userList = (List<T>) service.getMyFollowing();
                setRecyclerView(userList);
                break;

            case 2:
                if (myFollowing == null){
                    myFollowing = service.getMyFollowing();
                }
                if (myFollowers == null) {
                    myFollowers = service.getMyFollowers();
                }
                userList = (List<T>) compare(myFollowing, myFollowers);
                setRecyclerView(userList);
                break;

            case 3:
                if (myFollowing == null){
                    myFollowing = service.getMyFollowing();
                }
                if (myFollowers == null) {
                    myFollowers = service.getMyFollowers();
                }
                userList = (List<T>) compare(myFollowers, myFollowing);
                setRecyclerView(userList);
                break;

            case 4:
                if (service.getLoggedUser().getMedia_count() > 0) {
                    if (myFollowers == null) {
                        myFollowers = service.getMyFollowers();
                    }
                    InstagramFeedItem firstItem = (InstagramFeedItem) service.getLoggedUserMedias(null).items.get(0);
                    userList = (List<T>) Compare.compare(myFollowers, service.getMediaLikers(firstItem.pk));
                    setRecyclerView(userList);
                }
                break;

            case 5:
                userList = (List<T>) service.getFollowers(pk);
                setRecyclerView(userList);
                break;

            case 6:
                userList = (List<T>) service.getFollowing(pk);
                setRecyclerView(userList);
                break;

            case 7:
                usersFollowers = service.getFollowers(pk);
                usersFollowings = service.getFollowing(pk);
                userList = (List<T>) compare(usersFollowings,usersFollowers);
                setRecyclerView(userList);
                break;

            case 8:
                usersFollowers = service.getFollowers(pk);
                usersFollowings = service.getFollowing(pk);
                userList = (List<T>) compare(usersFollowers,usersFollowings);
                setRecyclerView(userList);
                break;
        }
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

}

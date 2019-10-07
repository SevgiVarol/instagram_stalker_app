package com.example.appinsta;


import android.annotation.SuppressLint;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.SearchView;

import com.example.appinsta.UserPage.UserProfile;

import java.util.List;

import dev.niekirk.com.instagram4android.Instagram4Android;
import dev.niekirk.com.instagram4android.InstagramConstants;
import dev.niekirk.com.instagram4android.requests.payload.InstagramUserSummary;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements RecyclerSearch.OnListener {

    private RecyclerSearch adapter;
    AutoCompleteTextView autoCompleteTextView;
    List<InstagramUserSummary> userSummary;
    EditText searchEdit;
    Instagram4Android instagram4Android;
    public SearchFragment() {
        // Required empty public constructor
    }
    @SuppressLint("ValidFragment")
    public SearchFragment(List<InstagramUserSummary> userSummary) {
        // Required empty public constructor
        this.userSummary=userSummary;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);

        View v= inflater.inflate(R.layout.search_fragment, container, false);

        //Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        searchEdit= (EditText)v.findViewById(R.id.editTextSearch);

        searchEdit.addTextChangedListener(new TextWatcher() {

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

        });

        RecyclerView recyclerView = v.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(8);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        if(userSummary!=null){
        adapter = new RecyclerSearch(userSummary,getActivity());


        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new RecyclerSearch.OnListener() {
            @Override
            public void onClick(int position) {

                UserProfile.urlOfUserPhotos.clear();
                InstagramConstants.userProfile=true;
                UserProfile fragment = new UserProfile(userSummary.get(position));
                FragmentManager manager = getFragmentManager();
                manager.beginTransaction().replace(R.id.linearLayout, fragment).addToBackStack("tag").commit();

            }
        });}

        return v;
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) item.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });


    }

    @Override
    public void onClick(int position) {

    }
}

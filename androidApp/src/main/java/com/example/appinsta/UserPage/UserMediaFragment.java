package com.example.appinsta.UserPage;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.appinsta.R;
import com.example.appinsta.UserPage.ImageAdapter;
import com.example.appinsta.UserPage.UserProfile;


public class UserMediaFragment extends Fragment {


    static GridView gridView;
    public UserMediaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_post, container, false);
        gridView=(GridView)view.findViewById(R.id.gridViewUser);



        gridView.setAdapter(new ImageAdapter(getActivity(), UserProfile.urlOfUserPhotos));



        return view;
    }

}

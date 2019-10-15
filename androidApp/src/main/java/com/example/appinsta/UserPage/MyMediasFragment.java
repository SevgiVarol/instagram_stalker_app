package com.example.appinsta.UserPage;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.appinsta.R;

public class MyMediasFragment extends Fragment {

    static GridView gridView;
    public MyMediasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.my_media_fragment, container, false);

        gridView=(GridView)view.findViewById(R.id.gridView);



       // gridView.setAdapter(new ImageAdapter(getActivity(), UserProfile.urlOfMyPhotos));
        return view;

    }

}

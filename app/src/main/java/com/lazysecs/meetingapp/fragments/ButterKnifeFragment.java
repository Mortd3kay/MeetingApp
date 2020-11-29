package com.lazysecs.meetingapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lazysecs.meetingapp.R;

import butterknife.ButterKnife;


public class ButterKnifeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_butter_knife, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}

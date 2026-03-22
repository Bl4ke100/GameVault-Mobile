package com.blake.gamevault.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blake.gamevault.R;
import com.blake.gamevault.databinding.FragmentGamesBinding;


public class GamesFragment extends Fragment {

    private FragmentGamesBinding binding;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGamesBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;

    }
}
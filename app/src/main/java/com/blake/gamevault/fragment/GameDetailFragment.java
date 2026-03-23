package com.blake.gamevault.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blake.gamevault.R;
import com.blake.gamevault.databinding.FragmentGameDetailBinding;


public class GameDetailFragment extends Fragment {

    private FragmentGameDetailBinding binding;
    private String gameId;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            gameId = getArguments().getString(gameId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGameDetailBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;

    }
}
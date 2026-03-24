package com.blake.gamevault.fragment;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blake.gamevault.R;
import com.blake.gamevault.adapter.GameSliderAdapter;
import com.blake.gamevault.databinding.FragmentGameDetailBinding;
import com.blake.gamevault.model.Game;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


public class GameDetailFragment extends Fragment {

    private FragmentGameDetailBinding binding;
    private String gameId;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            gameId = getArguments().getString("gameId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGameDetailBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().findViewById(R.id.bottomNav).setVisibility(View.GONE);

        getActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().getSupportFragmentManager().popBackStack();
                getActivity().findViewById(R.id.bottomNav).setVisibility(View.VISIBLE);
            }
        });

        //load game details

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("games")
                .whereEqualTo("gameId", gameId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot qds) {
                        if (!qds.isEmpty()) {
                            Game game = qds.getDocuments().get(0).toObject(Game.class);

                            GameSliderAdapter adapter = new GameSliderAdapter(game.getImages());
                            binding.gameImageSlider.setAdapter(adapter);
                            binding.gameName.setText(game.getTitle());
                            binding.gamePrice.setText(String.valueOf("LKR "+game.getPrice()+"0"));
                            binding.detailReleaseYear.setText(String.valueOf(game.getReleasedYear()));
                            binding.detailDescription.setText(game.getDescription());
                            binding.detailStockCount.setText(game.getStock() + " Keys Available");
                            binding.detailRating.setRating(game.getRating());
                            binding.dotsIndicator.attachTo(binding.gameImageSlider);


                            FirebaseFirestore.getInstance().collection("developers")
                                    .whereEqualTo("developerId", game.getDeveloperId())
                                    .get()
                                    .addOnSuccessListener(querySnapshot -> {
                                        Log.d("TAG", "developerId from game: " + game.getDeveloperId());
                                        Log.d("TAG", "query result size: " + querySnapshot.size());
                                        if (!querySnapshot.isEmpty()) {
                                            String developerName = querySnapshot.getDocuments().get(0).getString("name");
                                            Log.d("TAG", "developer name: " + developerName);
                                            binding.detailDeveloper.setText(developerName);
                                        }
                                    })
                                    .addOnFailureListener(e -> Log.e("TAG", "Error: " + e.getMessage()));

                            FirebaseFirestore.getInstance().collection("categories")
                                    .whereEqualTo("catId", game.getCategoryId())
                                    .get()
                                    .addOnSuccessListener(querySnapshot -> {
                                        if (!querySnapshot.isEmpty()) {
                                            String categoryName = querySnapshot.getDocuments().get(0).getString("name");
                                            binding.detailGenre.setText(categoryName);
                                        }
                                    });

                        }

                    }
                });
    }
}
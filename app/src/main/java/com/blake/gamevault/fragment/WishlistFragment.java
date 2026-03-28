package com.blake.gamevault.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.blake.gamevault.R;
import com.blake.gamevault.adapter.FavoriteAdapter;
import com.blake.gamevault.databinding.FragmentWishlistBinding;
import com.blake.gamevault.databinding.FragmentWishlistBinding;
import com.blake.gamevault.model.Game;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class WishlistFragment extends Fragment {

    private FragmentWishlistBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FavoriteAdapter adapter;
    private List<Game> favoriteGames;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentWishlistBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        favoriteGames = new ArrayList<>();

        binding.favRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));

        loadFavorites();
    }

    private void loadFavorites() {
        if (auth.getCurrentUser() == null) return;
        String uid = auth.getCurrentUser().getUid();

        // 1. Get the list of Favorite Game IDs
        db.collection("users").document(uid).collection("favorites")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        showEmptyState(true);
                        return;
                    }

                    showEmptyState(false);
                    List<String> gameIds = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        gameIds.add(doc.getId()); // We used gameId as the document ID!
                    }

                    fetchGameDetails(gameIds);
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to load favorites", Toast.LENGTH_SHORT).show());
    }

    private void fetchGameDetails(List<String> gameIds) {
        favoriteGames.clear();

        // Fetch each game individually to bypass the 10-item limit of 'whereIn'
        for (int i = 0; i < gameIds.size(); i++) {
            String id = gameIds.get(i);
            int finalI = i; // Needed for the inner callback

            db.collection("games").whereEqualTo("gameId", id).get()
                    .addOnSuccessListener(gameSnap -> {
                        if (!gameSnap.isEmpty()) {
                            Game game = gameSnap.getDocuments().get(0).toObject(Game.class);
                            if (game != null) favoriteGames.add(game);
                        }

                        // Once the last query finishes, set the adapter
                        if (finalI == gameIds.size() - 1) {
                            setupAdapter();
                        }
                    });
        }
    }

    private void setupAdapter() {
        if (favoriteGames.isEmpty()) {
            showEmptyState(true);
            return;
        }

        adapter = new FavoriteAdapter(favoriteGames, new FavoriteAdapter.OnFavoriteClickListener() {
            @Override
            public void onGameClick(Game game) {
                // Navigate to Game Detail
                Bundle bundle = new Bundle();
                bundle.putString("gameId", game.getGameId());
                bundle.putString("catId", game.getCategoryId());

                GameDetailFragment detailFragment = new GameDetailFragment();
                detailFragment.setArguments(bundle);

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, detailFragment)
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public void onRemoveClick(Game game, int position) {
                removeFavorite(game, position);
            }
        });

        binding.favRecycler.setAdapter(adapter);
    }

    private void removeFavorite(Game game, int position) {
        String uid = auth.getCurrentUser().getUid();

        // 1. Delete from Firestore
        db.collection("users").document(uid).collection("favorites").document(game.getGameId())
                .delete()
                .addOnSuccessListener(unused -> {
                    // 2. Remove from RecyclerView dynamically
                    adapter.removeGame(position);
                    Toast.makeText(getContext(), "Removed from favorites", Toast.LENGTH_SHORT).show();

                    // 3. Check if list is empty now
                    if (favoriteGames.isEmpty()) {
                        showEmptyState(true);
                    }
                });
    }

    private void showEmptyState(boolean isEmpty) {
        if (isEmpty) {
            binding.favRecycler.setVisibility(View.GONE);
            binding.favEmptyState.setVisibility(View.VISIBLE);
        } else {
            binding.favRecycler.setVisibility(View.VISIBLE);
            binding.favEmptyState.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
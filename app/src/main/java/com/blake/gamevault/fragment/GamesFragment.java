package com.blake.gamevault.fragment;
import android.os.Bundle;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.blake.gamevault.R;
import com.blake.gamevault.adapter.ListingAdapter;
import com.blake.gamevault.databinding.FragmentGamesBinding;
import com.blake.gamevault.model.Game;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
public class GamesFragment extends Fragment {
    private FragmentGamesBinding binding;
    private ListingAdapter adapter;
    private String catId;
    private List<Game> fullGameList = new ArrayList<>();
    private List<Game> displayedGameList = new ArrayList<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            catId = getArguments().getString("catId");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGamesBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.recyclerGameView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("games").get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!isAdded() || binding == null) return;
            WriteBatch batch = db.batch();
            Set<String> seenTitles = new HashSet<>();
            int deleteCount = 0;
            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                String title = doc.getString("title");
                if (title != null) {
                    if (seenTitles.contains(title)) {
                        batch.delete(doc.getReference());
                        deleteCount++;
                    } else {
                        seenTitles.add(title);
                    }
                }
            }
            if (deleteCount > 0) {
                final int finalCount = deleteCount;
                batch.commit().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.i("Games Fragment", "Cleaned up " + finalCount + " duplicates!");
                    } else {
                        Log.i("Games Fragment", "Failed to delete duplicates");
                    }
                });
            } else {
                Log.i("Games Fragment", "No duplicates found!");
            }
        });
        if (catId != null) {
            db.collection("games")
                    .whereEqualTo("categoryId", catId)
                    .orderBy("title", Query.Direction.ASCENDING)
                    .get()
                    .addOnSuccessListener(ds -> {
                        if (!isAdded() || binding == null) return;
                        binding.shimmerGames.stopShimmer();
                        binding.shimmerGames.setVisibility(View.GONE);
                        binding.recyclerGameView.setVisibility(View.VISIBLE);
                        if (!ds.isEmpty()) {
                            List<Game> games = ds.toObjects(Game.class);
                            adapter = new ListingAdapter(games, game -> {
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("gameId", game.getGameId());
                                bundle.putSerializable("catId", game.getCategoryId());
                                GameDetailFragment gameDetailFragment = new GameDetailFragment();
                                gameDetailFragment.setArguments(bundle);
                                getParentFragmentManager().beginTransaction()
                                        .replace(R.id.fragmentContainer, gameDetailFragment)
                                        .addToBackStack(null)
                                        .commit();
                            });
                            binding.recyclerGameView.setAdapter(adapter);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Firestore", "Error" + e.getMessage());
                            Toast.makeText(getContext(), "Check your internet connection", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            db.collection("games")
                    .orderBy("title", Query.Direction.ASCENDING)
                    .get()
                    .addOnSuccessListener(ds -> {
                        if (!isAdded() || binding == null) return;
                        binding.shimmerGames.stopShimmer();
                        binding.shimmerGames.setVisibility(View.GONE);
                        binding.recyclerGameView.setVisibility(View.VISIBLE);
                        if (!ds.isEmpty()) {
                            List<Game> games = ds.toObjects(Game.class);
                            adapter = new ListingAdapter(games, game -> {
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("gameId", game.getGameId());
                                bundle.putString("catId", game.getCategoryId());
                                GameDetailFragment gameDetailFragment = new GameDetailFragment();
                                gameDetailFragment.setArguments(bundle);
                                getParentFragmentManager().beginTransaction()
                                        .replace(R.id.fragmentContainer, gameDetailFragment)
                                        .addToBackStack(null)
                                        .commit();
                            });
                            binding.recyclerGameView.setAdapter(adapter);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Firestore", "Error" + e.getMessage());
                            Toast.makeText(getContext(), "Check your internet connection", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        getActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
        requireActivity().findViewById(R.id.toolBar).setVisibility(View.GONE);
        binding.recyclerGameView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        binding.btnGamesBack.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());
        binding.btnSortGames.setOnClickListener(v -> {
            String[] options = {"Price: Low to High", "Price: High to Low", "Title: A-Z"};
            new android.app.AlertDialog.Builder(getContext())
                    .setTitle("Sort By")
                    .setItems(options, (dialog, which) -> {
                        if (which == 0) {
                            displayedGameList.sort((g1, g2) -> Double.compare(g1.getPrice(), g2.getPrice()));
                        } else if (which == 1) {
                            displayedGameList.sort((g1, g2) -> Double.compare(g2.getPrice(), g1.getPrice()));
                        } else if (which == 2) {
                            displayedGameList.sort((g1, g2) -> g1.getTitle().compareToIgnoreCase(g2.getTitle()));
                        }
                        adapter.notifyDataSetChanged();
                    }).show();
        });
        binding.gamesLocalSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(android.text.Editable s) {
                String query = s.toString().toLowerCase().trim();
                displayedGameList.clear();
                if (query.isEmpty()) {
                    displayedGameList.addAll(fullGameList);
                } else {
                    for (Game game : fullGameList) {
                        if (game.getTitle().toLowerCase().contains(query)) {
                            displayedGameList.add(game);
                        }
                    }
                }
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
        });
        Query query = (catId != null)
                ? db.collection("games").whereEqualTo("categoryId", catId).orderBy("title", Query.Direction.ASCENDING)
                : db.collection("games").orderBy("title", Query.Direction.ASCENDING);
        query.get().addOnSuccessListener(ds -> {
            if (!isAdded() || binding == null) return;
            binding.shimmerGames.stopShimmer();
            binding.shimmerGames.setVisibility(View.GONE);
            binding.recyclerGameView.setVisibility(View.VISIBLE);
            if (!ds.isEmpty()) {
                fullGameList = ds.toObjects(Game.class);
                displayedGameList.clear();
                displayedGameList.addAll(fullGameList);
                adapter = new ListingAdapter(displayedGameList, game -> {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("gameId", game.getGameId());
                    bundle.putString("catId", game.getCategoryId());
                    GameDetailFragment gameDetailFragment = new GameDetailFragment();
                    gameDetailFragment.setArguments(bundle);
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainer, gameDetailFragment)
                            .addToBackStack(null)
                            .commit();
                });
                binding.recyclerGameView.setAdapter(adapter);
            }
        }).addOnFailureListener(e -> {
            if (isAdded() && getContext() != null) {
                Log.e("Firestore", "Error" + e.getMessage());
                Toast.makeText(getContext(), "Check your internet connection", Toast.LENGTH_SHORT).show();
            }
        });
        getActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().findViewById(R.id.toolBar).setVisibility(View.VISIBLE);
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() != null) {
            getActivity().findViewById(R.id.toolBar).setVisibility(View.VISIBLE);
        }
        binding = null;}
}
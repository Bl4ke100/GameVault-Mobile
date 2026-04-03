package com.blake.gamevault.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blake.gamevault.R;
import com.blake.gamevault.adapter.CategoryAdapter;
import com.blake.gamevault.databinding.FragmentShopBinding;
import com.blake.gamevault.model.Category;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class ShopFragment extends Fragment {

    private FragmentShopBinding binding;
    private CategoryAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentShopBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.recyclerCatView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("categories").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        // 🛑 THE SHIELD: Stop if the fragment is dead
                        if (!isAdded() || binding == null) return;

                        if (task.isSuccessful() && task.getResult() != null) {
                            QuerySnapshot result = task.getResult();
                            List<Category> categories = result.toObjects(Category.class);
                            adapter = new CategoryAdapter(categories, category -> {

                                Bundle bundle = new Bundle();
                                bundle.putString("catId", category.getCatId());

                                GamesFragment gamesFragment = new GamesFragment();
                                gamesFragment.setArguments(bundle);

                                getParentFragmentManager().beginTransaction()
                                        .replace(R.id.fragmentContainer, gamesFragment)
                                        .addToBackStack(null)
                                        .commit();

                            });
                            binding.recyclerCatView.setAdapter(adapter);
                        }
                    }
                });


        binding.btnSeeAllGames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new GamesFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    // 🛑 CRITICAL: Prevent memory leaks and crashes
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
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
        View view = binding.getRoot();
        return view;
        }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.recyclerCatView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        FirebaseFirestore db = FirebaseFirestore.getInstance();

//        Publisher pub1 = new Publisher("dev1", "Rockstar","");
//        Publisher pub2 = new Publisher("dev2", "Ubisoft","");
//        Publisher pub3 = new Publisher("dev3", "Activition","");
//        Publisher pub4 = new Publisher("dev4", "Sony Interactive Entertainment","");
//        Publisher pub5 = new Publisher("dev5", "Electronic Arts","");
//        Publisher pub6 = new Publisher("dev6", "Bethesda Game Studios","");
//        Publisher pub7 = new Publisher("dev7", "Nintendo","");
//        Publisher pub8 = new Publisher("dev8", "Square Enix","");
//        Publisher pub9 = new Publisher("dev9", "Capcom","");
//        Publisher pub10 = new Publisher("dev10", "Naughty Dog","");
//        Publisher pub11 = new Publisher("dev11", "SEGA","");
//        Publisher pub12 = new Publisher("dev12", "Take-Two Interactive","");
//        Publisher pub13 = new Publisher("dev13", "Konami","");
//        Publisher pub14 = new Publisher("dev14", "CD Project Red","");
//
//
//
//
//        List<Publisher> pubs = List.of(pub1, pub2, pub3, pub4, pub5, pub6, pub7, pub8, pub9, pub10, pub11, pub12, pub13, pub14);
//
//        WriteBatch batch = db.batch();
//
//        for (Publisher p : pubs){
//            DocumentReference ref = db.collection("publishers").document();
//            batch. set(ref, p);
//        }
//
//        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()){
//                    Toast.makeText(getContext(), "Pubs added", Toast.LENGTH_SHORT).show();
//                }else {
//                    Toast.makeText(getContext(), "Pubs could not be added", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

        db.collection("categories").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
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
                });


                binding.btnSeeAllGames.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        GamesFragment gamesFragment = new GamesFragment();
                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.fragmentContainer, new GamesFragment())
                                .addToBackStack(null)
                                .commit();
                    }
                });
    }
}
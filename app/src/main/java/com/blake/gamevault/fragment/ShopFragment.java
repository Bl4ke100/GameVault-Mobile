package com.blake.gamevault.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.blake.gamevault.R;
import com.blake.gamevault.adapter.CategoryAdapter;
import com.blake.gamevault.databinding.FragmentShopBinding;
import com.blake.gamevault.model.Category;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

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

//        Category category1 = new Category("cat1","Action","");
//        Category category2 = new Category("cat2","Adventure","");
//        Category category3 = new Category("cat3","Shooting","");
//        Category category4 = new Category("cat4","Horror","");
//        Category category5 = new Category("cat5","RPG","");
//        Category category6 = new Category("cat6","Arcade","");
//        Category category7 = new Category("cat7","Shooting","");
//
//        List<Category> cats = List.of(category1, category2, category3, category4, category5, category6, category7);
//
//        WriteBatch batch = db.batch();
//
//        for (Category c : cats){
//            DocumentReference ref = db.collection("categories").document();
//            batch. set(ref, c);
//        }
//
//        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()){
//                    Toast.makeText(getContext(), "Cats added", Toast.LENGTH_SHORT).show();
//                }else {
//                    Toast.makeText(getContext(), "Cats could not be added", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

        db.collection("categories").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        QuerySnapshot result = task.getResult();
                        List<Category> categories = result.toObjects(Category.class);
                        adapter = new CategoryAdapter(categories);
                        binding.recyclerCatView.setAdapter(adapter);
                    }
                });
    }
}
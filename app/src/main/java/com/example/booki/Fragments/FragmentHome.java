package com.example.booki.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.booki.Models.books_Model;
import com.example.booki.R;
import com.example.booki.Sell;
import com.example.booki.Adapters.category_Adapter;
import com.google.android.material.chip.Chip;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class FragmentHome extends Fragment {

    private ViewPager2 examPager;
    private final Handler sliderHandler = new Handler();

    // Views
    private RecyclerView booksRecycler;
    private TextView tvSelectedCategory;

    // Current category
    private String currentCategory = "JEE";
    private FirebaseFirestore db;

    public FragmentHome() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    // all the hooks created in onViewCreated are accessible throughout the class.
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        // Initialize Views
        examPager = view.findViewById(R.id.examPager);
        view.findViewById(R.id.btn_sell).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Sell.class);
            startActivity(intent);
        });
        booksRecycler = view.findViewById(R.id.bookslistRecycler);
        tvSelectedCategory = view.findViewById(R.id.tvSelectedCategory);

        Chip chipJee = view.findViewById(R.id.chip_jee);
        Chip chipUpsc = view.findViewById(R.id.chip_upsc);
        Chip chipNeet = view.findViewById(R.id.chip_neet);


        // RecyclerView Setup:
        // setting the recycler view in grid format
        GridLayoutManager gridLayout = new GridLayoutManager(getContext(), 2);
        booksRecycler.setLayoutManager(gridLayout);
        // note card should be designed according to the given layout.

        // Default Load
        chipJee.setChecked(true);
        loadBooks("JEE");

        // Chip Click Listeners
        chipJee.setOnClickListener(v -> {
            currentCategory = "JEE";
            tvSelectedCategory.setText(R.string.jee_books);
            loadBooks(currentCategory);
        });

        chipUpsc.setOnClickListener(v -> {
            currentCategory = "UPSC";
            tvSelectedCategory.setText(R.string.upsc_books);
            loadBooks(currentCategory);
        });

        chipNeet.setOnClickListener(v -> {
            currentCategory = "NEET";
            tvSelectedCategory.setText(R.string.neet_books);
            loadBooks(currentCategory);
        });


    }


    private final Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            examPager.setCurrentItem(examPager.getCurrentItem() + 1);
        }
    };


    private void loadBooks(String category) {
        ArrayList<books_Model> ar = new ArrayList<>();
        db.collection("books")
                .whereEqualTo("category", category)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ar.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            books_Model book = document.toObject(books_Model.class);
                            ar.add(book);
                        }

                        // this adapter fetch the data but data setting on the card is according to the viewHolder.
                        category_Adapter adapter = new category_Adapter(ar, getContext());
                        booksRecycler.setAdapter(adapter);
                    } else {
                        Log.d("FragmentHome", "Error getting documents: ", task.getException());
                    }
                });
    }



}

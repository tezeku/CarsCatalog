package com.example.myapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class LikeFragment extends Fragment {

    private RecyclerView recyclerView;
    private MyViewModel viewModel;
    private FloatingActionButton floatBtn;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(MyViewModel.class);
        DBHelper db = new DBHelper(getContext());

        recyclerView = view.findViewById(R.id.likeRecyclerView);
        floatBtn = view.findViewById(R.id.myFloatBtn);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        viewModel.getLiked(db);

        floatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.deleteAllLiked(db);
            }
        });

        viewModel.likedList.observe(getViewLifecycleOwner(), carItems -> {
            if (!carItems.isEmpty()) {
                view.findViewById(R.id.likeImg).setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                floatBtn.setVisibility(View.VISIBLE);
                CarAdapter carAdapter = new CarAdapter(getContext(), carItems, viewModel);
                recyclerView.setAdapter(carAdapter);
                carAdapter.notifyDataSetChanged();
            } else {
                recyclerView.setVisibility(View.GONE);
                floatBtn.setVisibility(View.GONE);
                view.findViewById(R.id.likeImg).setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_like, container, false);
    }
}
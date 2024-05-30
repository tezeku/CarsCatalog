package com.example.myapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LikeFragment extends Fragment {

    private RecyclerView recyclerView;
    private MyViewModel viewModel;
    private FloatingActionButton floatBtn;
    private TextView userEmailText;
    private Button logoutBtn;
    private Button regBtnOnLike;
    private Boolean isAuthorized;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            isAuthorized = true;
        } else {
            isAuthorized = false;
        }

        viewModel = new ViewModelProvider(requireActivity()).get(MyViewModel.class);
        DBHelper db = new DBHelper(getContext());

        recyclerView = view.findViewById(R.id.likeRecyclerView);
        floatBtn = view.findViewById(R.id.myFloatBtn);
        userEmailText = view.findViewById(R.id.userTextIdOnLiked);
        logoutBtn = view.findViewById(R.id.logoutBtnOnLiked);
        regBtnOnLike = view.findViewById(R.id.regBtnOnLiked);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        viewModel.getLiked(db);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String email = currentUser.getEmail();
            userEmailText.setText(email);
            Log.d("Current User Email", email);
        } else {
            Log.e("Current User Email", "User is not logged in");
        }

        if (userEmailText.getText().toString().equals("Вы не авторизованы")) {
            logoutBtn.setVisibility(View.GONE);
            regBtnOnLike.setVisibility(View.VISIBLE);
        } else {
            logoutBtn.setVisibility(View.VISIBLE);
            regBtnOnLike.setVisibility(View.GONE);
        }

        regBtnOnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), LoginSigninActivity.class));
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), LoginSigninActivity.class));
            }
        });

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
                CarAdapter carAdapter = new CarAdapter(getContext(), carItems, viewModel, isAuthorized);
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
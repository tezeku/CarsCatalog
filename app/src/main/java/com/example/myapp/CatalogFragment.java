package com.example.myapp;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.search.SearchBar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class CatalogFragment extends Fragment {
    private RecyclerView recyclerView;
    private MyViewModel viewModel;
    CarAdapter carAdapter;
    private androidx.appcompat.widget.SearchView searchView;
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
        viewModel.getList(db);

        if (viewModel.list.getValue().isEmpty()) {
            db.setDataInDB();
            viewModel.getList(db);
        }

        searchView = view.findViewById(R.id.searchView);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });

        recyclerView = view.findViewById(R.id.catalogRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        viewModel.list.observe(getViewLifecycleOwner(), carItems -> {
            carAdapter = new CarAdapter(getContext(), carItems, viewModel, isAuthorized);
            recyclerView.setAdapter(carAdapter);
            carAdapter.notifyDataSetChanged();
        });
    }

    private void filterList(String text) {
        List<CarItem> filteredList = new ArrayList<>();
        for (CarItem item : viewModel.list.getValue()) {
            if (item.getCarBrand().toLowerCase().contains(text.toLowerCase()) ||
                    item.getCarModel().toLowerCase().contains(text.toLowerCase()) ||
                    String.valueOf(item.getCarYear()).toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(getContext(), "Не найдено", Toast.LENGTH_SHORT).show();
        } else {
            carAdapter.setFilteredList(filteredList);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_catalog, container, false);
    }
}
package com.example.myapp;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
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
    private ChipGroup chipGroup;

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

        chipGroup = view.findViewById(R.id.gropChipOnCat);
        List<String> tmpChips = new ArrayList<>();
        List<String> selectedChips = new ArrayList<>();

        for (CarItem item : viewModel.list.getValue()) {
            if (!tmpChips.contains(item.getCarBrand())) {
                Log.e("chip", item.getCarBrand());
                tmpChips.add(item.getCarBrand());
                Chip chip = new Chip(getContext(), null, com.google.android.material.R.style.Widget_Material3_Chip_Filter); // Создаем новый экземпляр Chip, где context - контекст вашей активности или фрагмента
                chip.setCheckable(true);
                chip.setText(item.getCarBrand()); // Устанавливаем текст для Chip
                chip.setChipBackgroundColorResource(R.color.color_for_chip);

                // Добавляем обработчик нажатия на Chip
                chip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Обработка нажатия на Chip
                        Chip clickedChip = (Chip) v;
                        String clickedChipText = clickedChip.getText().toString();

                        if(selectedChips.contains(clickedChipText)) {
                            selectedChips.remove(clickedChipText);
                        } else {
                            selectedChips.add(clickedChipText);
                        }
                        filterListForChips(selectedChips);
                        /*
                        Chip clickedChip = (Chip) v;
                        String clickedChipText = clickedChip.getText().toString(); // Получаем текст нажатого Chip
                        Log.d("Chip Clicked", clickedChipText);

                        filterList(clickedChipText);*/
                    }
                });

                chipGroup.addView(chip);
            }
        }

        viewModel.list.observe(getViewLifecycleOwner(), carItems -> {
            carAdapter = new CarAdapter(getContext(), carItems, viewModel, isAuthorized);
            recyclerView.setAdapter(carAdapter);
            carAdapter.notifyDataSetChanged();
        });
    }

    private String updateQuery(List<String> selectedChips) {
        StringBuilder queryBuilder = new StringBuilder();

        for (String chipText : selectedChips) {
            if (queryBuilder.length() > 0) {
                queryBuilder.append(" и ");
            }
            queryBuilder.append(chipText);
        }

        return queryBuilder.toString();
    }

    private void filterListForChips(List<String> selectedChips) {
        if (!selectedChips.isEmpty()) {
            List<CarItem> filteredList = new ArrayList<>();
            for (CarItem item : viewModel.list.getValue()) {
                for (String str : selectedChips) {
                    if (item.getCarBrand().toLowerCase().contains(str.toLowerCase())) {
                        filteredList.add(item);
                    }
                }
            }

            if (filteredList.isEmpty()) {
                Toast.makeText(getContext(), "Не найдено", Toast.LENGTH_SHORT).show();
            } else {
                carAdapter.setFilteredList(filteredList);
            }
        } else {
            filterList("");
        }
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
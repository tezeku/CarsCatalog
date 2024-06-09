package com.example.myapp;

import android.animation.Animator;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.search.SearchBar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CatalogFragment extends Fragment {
    private RecyclerView recyclerView;
    private MyViewModel viewModel;
    CarAdapter carAdapter;
    private androidx.appcompat.widget.SearchView searchView;
    private Boolean isAuthorized, flagForMonetka;
    private ChipGroup chipGroup;
    private Button monetkaBtn;

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


        monetkaBtn = view.findViewById(R.id.questionBtn);
        LottieAnimationView animaCoin = view.findViewById(R.id.animaQuest);
        Random r = new Random();
        animaCoin.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {
                flagForMonetka = r.nextInt(2) == 0 ? true : false;
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                animaCoin.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle(flagForMonetka ? "Можете брать!" : "Думаем, эта машина вам не подходит")
                        .setPositiveButton("Спасибо!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).show();
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {

            }
        });
        monetkaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle("Монетка!")
                        .setMessage("А это наша главная фишка - если вы сомневаетесь в выборе машины и готовы отдаться воле случая - монетка подскажет вам :)")
                        .setNegativeButton("Нет!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton("Давай!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                animaCoin.cancelAnimation();
                                animaCoin.playAnimation();
                                recyclerView.setVisibility(View.GONE);
                                animaCoin.setVisibility(View.VISIBLE);
                                dialog.cancel();
                            }
                        })
                        .show();
            }
        });

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
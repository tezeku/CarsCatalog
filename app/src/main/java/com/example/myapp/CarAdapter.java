package com.example.myapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarViewHolder> {

    Context context;
    List<CarItem> carsArrayList;
    MyViewModel viewModel;
    DBHelper db;
    Boolean expanded;

    public CarAdapter(Context context, List<CarItem> carsArrayList, MyViewModel viewModel) {
        this.context = context;
        this.carsArrayList = carsArrayList;
        this.viewModel = viewModel;
        db = new DBHelper(context);
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_car, parent, false);
        return new CarViewHolder(v);
    }

    public void setFilteredList(List<CarItem> filteredList) {
        this.carsArrayList = filteredList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        CarItem carItem = carsArrayList.get(position);

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carItem.setExpanded(!carItem.isExpanded());
                notifyItemChanged(position);
            }
        });

        Boolean isExpanded = carItem.isExpanded();
        holder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        if (db.searchInLiked(carItem) != null) {
            holder.likeBtn.setIconTintResource(R.color.md_theme_tertiaryFixed_mediumContrast);
            holder.likeBtn.setIconResource(R.drawable.like_fill);
        } else {
            holder.likeBtn.setIconTintResource(R.color.md_theme_tertiaryFixed_mediumContrast);
            holder.likeBtn.setIconResource(R.drawable.like);
        }
        Glide.with(holder.carThumbnail)
                .load(carItem.getCarThumbnail())
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(holder.carThumbnail);
        holder.carBrand.setText(carItem.getCarBrand());
        holder.carModel.setText(carItem.getCarModel());
        holder.carYear.setText(String.valueOf(carItem.getCarYear()));
        holder.carPrice.setText(String.valueOf(carItem.getPrice()) + " руб.");

        holder.specsDetails.setText(carItem.getCarInfo());
        holder.likeBtn.setOnClickListener(view -> {
            viewModel.manageLiked(db, carItem);
            viewModel.getLiked(db);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        if (carsArrayList != null) {
            return carsArrayList.size();
        } else return 0;

    }

    public static class CarViewHolder extends RecyclerView.ViewHolder {
        ImageView carThumbnail;
        TextView carBrand;
        TextView carModel;
        TextView carYear;
        TextView specsDetails;
        TextView carPrice;
        MaterialButton likeBtn;
        ConstraintLayout expandableLayout, mainLayout;

        public CarViewHolder(@NonNull View itemView) {
            super(itemView);

            carThumbnail = itemView.findViewById(R.id.carThumbnail);
            carBrand = itemView.findViewById(R.id.carBrand);
            carModel = itemView.findViewById(R.id.carModel);
            carYear = itemView.findViewById(R.id.carYear);
            carPrice = itemView.findViewById(R.id.textPriceCar);
            specsDetails = itemView.findViewById(R.id.textSpecsDetails);
            likeBtn = itemView.findViewById(R.id.likeBtn);
            expandableLayout = itemView.findViewById(R.id.expandableLayout);
            mainLayout = itemView.findViewById(R.id.bigLayout);
        }
    }
}

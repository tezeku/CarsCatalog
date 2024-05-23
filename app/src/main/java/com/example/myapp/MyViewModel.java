package com.example.myapp;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Collections;
import java.util.List;

public class MyViewModel extends ViewModel {
    public MutableLiveData<List<CarItem>> list = new MutableLiveData<>(Collections.emptyList());
    public MutableLiveData<List<CarItem>> likedList = new MutableLiveData<>(Collections.emptyList());

    public void getList(Context context, DBHelper db) {
        try {
            list.setValue(db.getData());
            Log.i("BBB", String.valueOf(list.getValue().size()));
        } catch (Exception e) {
            Log.i("AAA", e.getMessage());
        }
    }

    public void getLiked(Context context, DBHelper db) {
        try {
            likedList.setValue(db.getLiked());
            Log.i("BBB", String.valueOf(list.getValue().size()));
        } catch (Exception e) {
            Log.i("AAA", e.getMessage());
        }
    }

    public void manageLiked(DBHelper db, CarItem carItem) {
        try {
            likedList.setValue(db.getLiked());
            if (db.searchInLiked(carItem) != null) {
                db.removeOneLiked(carItem);
            } else {
                db.addLiked(carItem);
            }
            Log.i("BBB", String.valueOf(list.getValue().size()));
        } catch (Exception e) {
            Log.i("CCC", e.getMessage());
        }
    }
}

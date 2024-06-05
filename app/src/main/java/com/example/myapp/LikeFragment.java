package com.example.myapp;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LikeFragment extends Fragment {

    private RecyclerView recyclerView;
    private MyViewModel viewModel;
    private FloatingActionButton floatBtn;
    private TextView userEmailText;
    private Button logoutBtn;
    private Button regBtnOnLike;
    private Boolean isAuthorized;
    private LottieAnimationView logoutAnima;
    private LottieAnimationView enterOnLikedAnima;
    boolean animationCompleted = false;
    private TextView alText;

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
        logoutAnima = view.findViewById(R.id.logoutAnimation);
        enterOnLikedAnima = view.findViewById(R.id.enterAnimaOnLiked);
        alText = view.findViewById(R.id.likeImg);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String email = currentUser.getEmail();
            userEmailText.setText(email);
            logoutBtn.setVisibility(View.VISIBLE);
            regBtnOnLike.setVisibility(View.GONE);
            Log.d("Current User Email", email);
        } else {
            logoutBtn.setVisibility(View.GONE);
            regBtnOnLike.setVisibility(View.VISIBLE);
            alText.setText("Здесь вы могли бы видеть ваши избранные машины");
            Log.e("Current User Email", "User is not logged in");
        }

        if (currentUser != null && db.getLiked().isEmpty()) {
            enterOnLikedAnima.setVisibility(View.VISIBLE);
            enterOnLikedAnima.addAnimatorListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(@NonNull Animator animation) {}

                @Override
                public void onAnimationEnd(@NonNull Animator animation) {}

                @Override
                public void onAnimationCancel(@NonNull Animator animation) {
                    if (animationCompleted) {
                        viewModel.getLiked(db);
                        userEmailText.setVisibility(View.VISIBLE);
                        enterOnLikedAnima.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onAnimationRepeat(@NonNull Animator animation) {
                    FirebaseFirestore fbs = FirebaseFirestore.getInstance();
                    // Получаем ссылку на коллекцию "Users"
                    DocumentReference currentUserDoc = fbs.collection("Users").document(currentUser.getUid());
                    // Создаем коллекцию "Liked" для текущего пользователя
                    CollectionReference userLikedCollection = currentUserDoc.collection("Liked");

                    List<CarItem> userCars = new ArrayList<>();

                    userLikedCollection.get().addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                userCars.add(document.toObject(CarItem.class));
                                viewModel.setLiked(db,  document.toObject(CarItem.class));
                                Log.d("Firestore", "CarItem model: " + document.toObject(CarItem.class).getCarModel());
                            }
                        } else {
                            Log.d("Firestore", "Коллекция 'Liked' пуста");
                        }
                    });
                    enterOnLikedAnima.pauseAnimation();
                    animationCompleted = true;
                    userEmailText.setVisibility(View.VISIBLE);
                    enterOnLikedAnima.setVisibility(View.GONE);
                    Log.e("Zapoln", "animaend");
                }
            });
        } else {
            viewModel.getLiked(db);
            userEmailText.setVisibility(View.VISIBLE);
        }

        if (animationCompleted) {
            viewModel.getLiked(db);
            Log.e("animacomplIF", "VM getLiked");
            userEmailText.setVisibility(View.VISIBLE);
        }

        regBtnOnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), LoginSigninActivity.class));
            }
        });

        Log.e("tmpCarSize", String.valueOf(db.getLiked().size()));


        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutBtn.setVisibility(View.GONE);
                logoutAnima.setVisibility(View.VISIBLE);
                Log.e("NAAAA", "startAnima");
                logoutAnima.addAnimatorListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(@NonNull Animator animation) {
                        Log.e("AAAAAA", "startedANIMA");
                    }

                    @Override
                    public void onAnimationEnd(@NonNull Animator animation) {
                        Log.e("AAAAAA", "endANIMA");
                    }

                    @Override
                    public void onAnimationCancel(@NonNull Animator animation) {
                        Log.e("AAAAAA", "canceledANIMA");
                        startActivity(new Intent(getContext(), LoginSigninActivity.class));
                    }

                    @Override
                    public void onAnimationRepeat(@NonNull Animator animation) {
                        FirebaseFirestore fbs = FirebaseFirestore.getInstance();
                        // Получаем ссылку на коллекцию "Users"
                        DocumentReference currentUserDoc = fbs.collection("Users").document(currentUser.getUid());
                        // Создаем коллекцию "Liked" для текущего пользователя
                        CollectionReference userLikedCollection = currentUserDoc.collection("Liked");
                        Log.e("LIST AAA", db.getLiked().toString());
                        Log.e("LIST AAA", "success");

                        if (!db.getLiked().isEmpty()) {
                            // Удаляем все документы из коллекции "Liked"
                            userLikedCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        Log.e("tmpCarSize", String.valueOf(db.getLiked().size()));
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            document.getReference().delete();
                                            Log.d("Firestore", "Документ добавлен: " + document.getId());
                                        }
                                        // После успешного удаления старых данных добавляем новые
                                        List<CarItem> tmpLiked = new ArrayList<>(db.getLiked());
                                        Log.e("tmpCarSize", String.valueOf(tmpLiked.size()));
                                        for (CarItem item : tmpLiked) {
                                            userLikedCollection.add(item)
                                                    .addOnSuccessListener(documentReference -> Log.d("Firestore", "Документ добавлен: " + documentReference.getId()))
                                                    .addOnFailureListener(e -> Log.e("Firestore", "Ошибка добавления документа", e));
                                        }

                                    } else {
                                        Log.d("Firestore", "Ошибка при получении данных для удаления из коллекции 'Liked'");
                                    }
                                    db.removeLiked();
                                    FirebaseAuth.getInstance().signOut();
                                    logoutAnima.pauseAnimation();
                                    logoutAnima.cancelAnimation();
                                }
                            });
                        } else {
                            // Удаляем все документы из коллекции "Liked"
                            userLikedCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        Log.e("tmpCarSize", String.valueOf(db.getLiked().size()));
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            document.getReference().delete();
                                            Log.d("Firestore", "Документ добавлен: " + document.getId());
                                        }
                                    } else {
                                        Log.d("Firestore", "Ошибка при получении данных для удаления из коллекции 'Liked'");
                                    }
                                    db.removeLiked();
                                    FirebaseAuth.getInstance().signOut();
                                    logoutAnima.pauseAnimation();
                                    logoutAnima.cancelAnimation();
                                }
                            });
                        }
                    }
                });
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
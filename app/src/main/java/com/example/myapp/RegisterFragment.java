package com.example.myapp;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.myapp.databinding.FragmentRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Boolean isCreated;
    private Boolean toAcivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.backToLogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), LoginSigninActivity.class));
            }
        });

        binding.filledTonalButtonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.textEmailEditText.getText().toString().isEmpty() || binding.textPasswdEditText.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Поля не должны быть пустыми", Toast.LENGTH_LONG).show();
                } else if (binding.textPasswdEditText.getText().toString().isEmpty()) {

                }
                else {
                    binding.regImg.setVisibility(View.GONE);

                    binding.animationViewRegister.setVisibility(View.VISIBLE);
                    binding.animationViewRegister.addAnimatorListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(@NonNull Animator animation) {}

                        @Override
                        public void onAnimationEnd(@NonNull Animator animation) {}

                        @Override
                        public void onAnimationCancel(@NonNull Animator animation) {
                            if (isCreated != null && toAcivity != null) {
                                if (isCreated) {
                                    if (toAcivity) {
                                        startActivity(new Intent(getContext(), MainActivity.class));
                                    } else {
                                        Log.e("RegisterFragment", "Error writing document");
                                        Toast.makeText(getContext(), "Error writing document", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Log.e("RegisterFragment", "Error creating user");
                                    Toast.makeText(getContext(), "Error creating user", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onAnimationRepeat(@NonNull Animator animation) {
                            FirebaseAuth.getInstance().createUserWithEmailAndPassword(binding.textEmailEditText.getText().toString(), binding.textPasswdEditText.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                isCreated = true;
                                                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                                Log.e("UID", userID);
                                                Map<String, String> userData = new HashMap<>();
                                                userData.put("email", binding.textEmailEditText.getText().toString());

                                                db.collection("Users").document(userID)
                                                        .set(userData)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    toAcivity = true;
                                                                    binding.animationViewRegister.pauseAnimation();
                                                                    binding.animationViewRegister.cancelAnimation();
                                                                } else {
                                                                    toAcivity = false;
                                                                }
                                                            }
                                                        });
                                            } else {
                                                isCreated = false;
                                                binding.animationViewRegister.pauseAnimation();
                                                binding.animationViewRegister.cancelAnimation();
                                            }
                                        }
                                    });
                        }
                    });
                }
            }
        });
    }
}
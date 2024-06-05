package com.example.myapp;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapp.databinding.ActivityLoginSigninBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginSigninActivity extends AppCompatActivity {

    private FrameLayout frameLayout;
    private Button toReg;
    private Button toUnreg;
    private ActivityLoginSigninBinding binding;
    Boolean isAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLoginSigninBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot()); // Используем binding для установки макета

        binding.animationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {}

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {}

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {
                if (isAuth) {
                    startActivity(new Intent(LoginSigninActivity.this, MainActivity.class));
                } else {
                    binding.animationView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    isAuth = true;
                    binding.animationView.pauseAnimation();
                    binding.animationView.cancelAnimation();
                } else {
                    isAuth = false;
                    binding.animationView.pauseAnimation();
                    binding.animationView.cancelAnimation();
                }
            }
        });

        frameLayout = findViewById(R.id.frameLogReg);
        toReg = findViewById(R.id.textRegBtn);
        toUnreg = findViewById(R.id.textToGoUnreg);

        toUnreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginSigninActivity.this, MainActivity.class));
            }
        });

        // Используем getSupportFragmentManager() для получения FragmentManager
        final FragmentManager fragmentManager = getSupportFragmentManager();

        toReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frameLogReg, new RegisterFragment());
                fragmentTransaction.commit();
            }
        });

        // Обработчик нажатия кнопки "Войти"
        binding.filledTonalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.textEmailEditText.getText().toString().isEmpty() || binding.textPasswdEditText.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Поля не могут быть пустыми", Toast.LENGTH_LONG).show();
                } else {
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(binding.textEmailEditText.getText().toString(), binding.textPasswdEditText.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        startActivity(new Intent(LoginSigninActivity.this, MainActivity.class));
                                    }
                                }
                            });
                }
            }
        });
    }
}

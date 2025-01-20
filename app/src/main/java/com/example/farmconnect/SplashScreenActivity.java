package com.example.farmconnect;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.farmconnect.ExtraClasses.NavigatetoFragments;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashScreenActivity extends AppCompatActivity {
    private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);
        user= FirebaseAuth.getInstance().getCurrentUser();
        runAnimation(user != null);
    }
    private void gettype(FirebaseUser user){
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.collection("users")
                .document(user.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        DocumentSnapshot documentSnapshot= task.getResult();
                        startActivity(NavigatetoFragments.navigatetoPages(SplashScreenActivity.this,documentSnapshot.getString("type"),"No", MainPage.class));
                    }
                });
    }
    private void runAnimation(Boolean page){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(page){
                    gettype(user);
                } else{
                    startActivity(NavigatetoFragments.navigatetoPages(SplashScreenActivity.this,"","", MainActivity.class));
                }
            }
        },4000);
    }
}
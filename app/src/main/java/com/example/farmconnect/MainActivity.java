package com.example.farmconnect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void farmerlogin(View view) {
        moveToLogin("Farmer");
    }

    public void vendorlogin(View view) {
        moveToLogin("Vendor");
    }

    public void reasearchlogin(View view) {
        moveToLogin("Researcher");
    }
    private void moveToLogin(String type){
        startActivity(NavigatetoFragments.navigatetoPages(MainActivity.this,type,"No", LoginPage.class));
    }

    public void registerperson(View view) {
        startActivity(new Intent(this, RegisterPage.class));
    }
}
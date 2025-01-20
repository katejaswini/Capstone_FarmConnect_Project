package com.example.farmconnect;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.farmconnect.ExtraClasses.NavigatetoFragments;
import com.example.farmconnect.ExtraClasses.TextCheckOperation;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class LoginPage extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    EditText phone,otp;
    private String verificationID,resendtoken;
    Button verify;
    String type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);
        init();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sentotp=otp.getText().toString();
                TextCheckOperation.checkempty(sentotp,"OTP",LoginPage.this);
                TextCheckOperation.checkvalid(sentotp,otp,6,"Enter valid otp");
                PhoneAuthCredential credential= PhoneAuthProvider.getCredential(verificationID,sentotp);
                signin(credential);
            }
        });
    }
    private void init(){
        phone=findViewById(R.id.Login_PhoneNumber);
        otp=findViewById(R.id.Login_FillOtp);
        mAuth = FirebaseAuth.getInstance();
        verify=findViewById(R.id.Login_Verify_Otp);
        type=getIntent().getStringExtra("userType");
    }
    public void sendOtp(View view) {
        String phoneno="+1"+phone.getText().toString();
        TextCheckOperation.checkempty(phoneno,"Phone",LoginPage.this);
        TextCheckOperation.checkvalid(phoneno,phone,10,"Valid phone number is required");
        sendVerificationCode(phoneno);
    }
    public void sendVerificationCode(String number){
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(number)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signin(phoneAuthCredential);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(LoginPage.this, "Verification failed: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.d("Login",e.getMessage());
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                LoginPage.this.verificationID=s;
                                LoginPage.this.resendtoken= forceResendingToken.toString();
                                Toast.makeText(LoginPage.this, "OTP Sent", Toast.LENGTH_SHORT).show();
                            }
                        }).build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    public void signin(PhoneAuthCredential credential){
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this,task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(LoginPage.this, "Authentication successful", Toast.LENGTH_SHORT).show();
                        startActivity(NavigatetoFragments.navigatetoPages(LoginPage.this,type,"No", MainPage.class));
                    } else {
                        Toast.makeText(LoginPage.this,"Authentication failed",Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void registernewuser(View view) {
        startActivity(new Intent(this, RegisterPage.class));
    }
}
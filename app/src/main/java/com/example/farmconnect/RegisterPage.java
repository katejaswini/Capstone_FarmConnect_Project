package com.example.farmconnect;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.farmconnect.ExtraClasses.ConvertImage;
import com.example.farmconnect.ExtraClasses.NavigatetoFragments;
import com.example.farmconnect.ExtraClasses.TextCheckOperation;
import com.example.farmconnect.Model.User_Data_Model;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class RegisterPage extends AppCompatActivity {
    private EditText phone,otp,name;
    private AutoCompleteTextView state;
    private Spinner userType;
    private String verificationID,type;
    private Bitmap avatar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_page);
        init();
        ArrayAdapter adapter=new ArrayAdapter(this, android.R.layout.select_dialog_item,getResources().getTextArray(R.array.states));
        state.setAdapter(adapter);
        state.setThreshold(1);
        avatar = BitmapFactory.decodeResource(getResources(),R.drawable.logo);
        userType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void init(){
        phone=findViewById(R.id.Register_Phone);
        otp=findViewById(R.id.Register_Check_Otp);
        userType=findViewById(R.id.Register_UserType);
        name=findViewById(R.id.Register_Name);
        state=findViewById(R.id.Register_State);
        mAuth = FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
    }
    public void sentOtp(View view) {
        String phoneno="+1"+phone.getText().toString().trim();
        TextCheckOperation.checkempty(phoneno,"Phone",RegisterPage.this);
        TextCheckOperation.checkvalid(phoneno,phone,10,"Valid phone number is required");
        PhoneAuthOptions options=PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneno)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        signInCerdential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(RegisterPage.this, "Verification failed: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        RegisterPage.this.verificationID=s;
                        Toast.makeText(RegisterPage.this, "OTP Sent", Toast.LENGTH_SHORT).show();
                        otp.setVisibility(View.VISIBLE);
                        findViewById(R.id.Register_VerifyOtp).setVisibility(View.VISIBLE);
                        findViewById(R.id.Register_OtpLabel).setVisibility(View.VISIBLE);
                        findViewById(R.id.Register_Check_Otp).setVisibility(View.VISIBLE);
                        findViewById(R.id.RegisterPage_NameLabel).setVisibility(View.GONE);
                        findViewById(R.id.Register_StateLabel).setVisibility(View.GONE);
                        findViewById(R.id.Register_UserLabel).setVisibility(View.GONE);
                        findViewById(R.id.Register_PhoneLabel).setVisibility(View.GONE);
                        name.setVisibility(View.GONE);
                        state.setVisibility(View.GONE);
                        phone.setVisibility(View.GONE);
                        userType.setVisibility(View.GONE);
                    }
                }).build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    public void verifyOtp(View view) {
        String verifyotp=otp.getText().toString().trim();
        TextCheckOperation.checkempty(verifyotp,"OTP",RegisterPage.this);
        TextCheckOperation.checkvalid(verifyotp,otp,6,"Enter valid otp");
        PhoneAuthCredential credential=PhoneAuthProvider.getCredential(verificationID,verifyotp);
        signInCerdential(credential);
    }
    public void addtodatabase(FirebaseUser user){
        User_Data_Model model=new User_Data_Model(user.getUid(),type,user.getPhoneNumber(),"Name", ConvertImage.getBase64String(avatar),"",new ArrayList<>(),name.getText().toString(),state.getText().toString());
        DocumentReference reference=db.collection("users").document(user.getUid());
        reference.set(model)
                .addOnSuccessListener((task)-> {
                    reference.collection("Buying").document("placeholder").set(new Message());
                    reference.collection("Bought").document("placeholder").set(new Message());
                    reference.collection("Sold").document("placeholder").set(new Message());
                    reference.collection("Selling").document("placeholder").set(new Message());
                    Toast.makeText(this, "Created account successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e->
                    Toast.makeText(this, "Failed to save userdata", Toast.LENGTH_SHORT).show()
                );
    }
    private void signInCerdential(PhoneAuthCredential credential){
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this,task -> {
                    if(task.isSuccessful()){
                        FirebaseUser user=task.getResult().getUser();
                        if(user!=null){
                            addtodatabase(user);
                        }
                        Toast.makeText(RegisterPage.this, "Authentication successful", Toast.LENGTH_SHORT).show();
                        startActivity(NavigatetoFragments.navigatetoPages(RegisterPage.this,type,"Yes", MainPage.class));
                    } else {
                        Toast.makeText(RegisterPage.this,"Authentication failed",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void registertologin(View view) {
        Intent intent=new Intent(RegisterPage.this, MainActivity.class);
        startActivity(intent);
    }
}
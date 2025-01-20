package com.example.farmconnect;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class SettingFragment extends Fragment {
    EditText newphonenumber,enterotp,oldphonenumber;
    Button sendOtp,verifyOtp,verifyOldOtp;
    String phone,otp;
    FirebaseAuth mAuth;
    FirebaseUser user;
    Boolean isnew=Boolean.FALSE;
    public SettingFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_setting, container, false);
        init(view);
        verifyOldOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone="+1"+oldphonenumber.getText().toString().trim();
                checkempty(phone,"phone");
                checkphoneno(phone,false);
            }
        });
        sendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone="+1"+newphonenumber.getText().toString().trim();
                checkempty(phone,"New Phone");
                checkphoneno(phone,true);
            }
        });
        verifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sentotp=enterotp.getText().toString();
                checkempty(sentotp,"OTP");
                if(sentotp.length()<6){
                    enterotp.setError("Enter valid otp");
                    enterotp.requestFocus();
                    return;
                }
                PhoneAuthCredential credential= PhoneAuthProvider.getCredential(otp,sentotp);
                verifyPhoneNumberWithCredential(credential);
            }
        });
        return view;
    }
    public void init(View view){
        newphonenumber=view.findViewById(R.id.Setting_PhoneNumber);
        enterotp=view.findViewById(R.id.Setting_Otp);
        oldphonenumber=view.findViewById(R.id.Setting_OldPhoneNumber);
        sendOtp=view.findViewById(R.id.Setting_SendOtp);
        verifyOtp=view.findViewById(R.id.Settings_Verify_Otp);
        verifyOldOtp=view.findViewById(R.id.Setting_VerifyOldNo);
        mAuth=FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mAuth=FirebaseAuth.getInstance();
    }
    public void checkempty(String text,String box){
        if(TextUtils.isEmpty(text)){
            Toast.makeText(requireContext(),box+" is empty",Toast.LENGTH_SHORT).show();
            return;
        }
    }
    public void checkphoneno(String text,Boolean isold){
        PhoneAuthOptions options= PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(text)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(requireActivity())
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        if (phoneAuthCredential != null) {
                            verifyPhoneNumberWithCredential(phoneAuthCredential);
                        } else {
                            Toast.makeText(requireContext(), "Invalid OTP", Toast.LENGTH_SHORT).show();
                        }

                    }
                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(requireContext(), "Verification failed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        SettingFragment.this.otp=s;
                        Toast.makeText(requireContext(), "OTP Sent", Toast.LENGTH_SHORT).show();
                    }
                }).build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    public void verifyPhoneNumberWithCredential(PhoneAuthCredential credential) {
        if (credential != null) {
            oldphonenumber.setText("");
            enterotp.setText("");
            isnew=Boolean.TRUE;
            Toast.makeText(requireContext(), "OTP is correct", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Invalid OTP", Toast.LENGTH_SHORT).show();
        }
    }
}
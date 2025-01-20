package com.example.farmconnect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.farmconnect.ExtraClasses.ConvertImage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;

public class ProfileFragment extends Fragment {
    TextView name,location;
    Button Addlocation,saveProfile,changeuserimage;
    LinearLayout locationlist;
    ImageView imageView;
    int textCount=0;
    ArrayList<String> locationsArrayList;
    private FirebaseUser user;
    private FirebaseFirestore db;
    public ProfileFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_profile, container, false);
        init(view);
        changeuserimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                launchSomeActivity.launch(intent);
            }
            ActivityResultLauncher<Intent> launchSomeActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode()
                        == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.getData() != null) {
                        Uri selectedImageUri = data.getData();
                        Bitmap selectedImageBitmap = null;
                        try {
                            selectedImageBitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), selectedImageUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        imageView.setImageBitmap(selectedImageBitmap);
                    }
                }
            });
        });
        Addlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String locationname=location.getText().toString();
                if(locationname.isEmpty()){
                    Toast.makeText(view.getContext(), "Enter Location", Toast.LENGTH_SHORT).show();
                }
                createLayout(locationname,view.getContext());
                locationsArrayList.add(locationname);
                location.setText("");
            }
        });
        db.collection("users")
                .document(user.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        DocumentSnapshot documentSnapshot= task.getResult();
                        if(documentSnapshot.exists()){
                            ArrayList<String> ArrayList=(ArrayList<String>) documentSnapshot.get("location");
                            if(!ArrayList.isEmpty()){
                                textCount=ArrayList.size();
                                ArrayList.forEach(v->{
                                    createLayout(v,view.getContext());
                                });
                            } else {
                                textCount=0;
                            }
                        }
                    }
                });
        saveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String profilename=name.getText().toString();
                if(profilename.isEmpty()){
                    Toast.makeText(requireContext(), "Enter name", Toast.LENGTH_SHORT).show();
                    return;
                }
                Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                db.collection("users")
                        .document(user.getUid())
                        .update("name",profilename,"location",locationsArrayList,"image", ConvertImage.getBase64String(bitmap))
                        .addOnSuccessListener(task->{
                            Toast.makeText(requireContext(), "Successfully updated", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(requireContext(), "Could not be updated", Toast.LENGTH_SHORT).show();
                        });
            }
        });
        return view;
    }
    public void init(View view){
        name=view.findViewById(R.id.Profile_Name);
        location=view.findViewById(R.id.Profile_Location);
        Addlocation=view.findViewById(R.id.Profile_AddLocation);
        locationlist=view.findViewById(R.id.Profile_LocationList);
        imageView=view.findViewById(R.id.Profile_UserImage);
        changeuserimage=view.findViewById(R.id.Profile_ChangeProfile);
        saveProfile=view.findViewById(R.id.Profile_Save);
        user= FirebaseAuth.getInstance().getCurrentUser();
        locationsArrayList=new ArrayList<>();
        db=FirebaseFirestore.getInstance();
    }
    public void createLayout(String locationname, Context context){
        if(textCount>3){
            Toast.makeText(requireContext(), "Maximum of 3 text allowed", Toast.LENGTH_SHORT).show();
        } else{
            RelativeLayout relativeLayout = new RelativeLayout(context);
            RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            relativeLayout.setLayoutParams(relativeLayoutParams);

            TextView newlocation=new TextView(context);
            newlocation.setText(locationname);
            newlocation.setId(View.generateViewId());
            newlocation.setTextSize(20);

            ImageButton closeButton = new ImageButton(context);
            closeButton.setImageResource(R.drawable.baseline_close_24); // Set the close icon
            closeButton.setLayoutParams(new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
            closeButton.setPadding(10, 10, 10, 10);

            RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            textParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

            // Add the TextView to RelativeLayout
            relativeLayout.addView(newlocation, textParams);

            // Set position for the close button next to the TextView
            RelativeLayout.LayoutParams closeParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            closeParams.addRule(RelativeLayout.RIGHT_OF, newlocation.getId());

            // Add the ImageButton to RelativeLayout
            relativeLayout.addView(closeButton,closeParams);
            locationlist.addView(relativeLayout);
            // Increase the count of added texts
            textCount++;

            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    locationlist.removeView(relativeLayout);
                    locationsArrayList.remove(locationname);
                    textCount--;
                }
            });
        }
    }
}
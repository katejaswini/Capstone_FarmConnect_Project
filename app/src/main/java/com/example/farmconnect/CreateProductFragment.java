package com.example.farmconnect;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.farmconnect.ExtraClasses.ConvertImage;
import com.example.farmconnect.ExtraClasses.DatabaseConnection;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CreateProductFragment extends Fragment {
    EditText productname,productamount,productrenttime,productstock;
    TextView durationLabel;
    ImageView productimage;
    Spinner productlocation,productbuyorrent,productmeasure,productdeliverymode,productDuration,productCategory;
    AdapterView.OnItemSelectedListener listener;
    Button productsell;
    FloatingActionButton addfeatures;
    LinearLayout featureslist;
    String buyorent,location,deliverymode,measure,duration,category;
    ArrayList<String> features;
    private String userid;
    private FirebaseFirestore db;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_create_product, container, false);
        init(view);
        productimage.setOnClickListener(new View.OnClickListener() {
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
                        productimage.setImageBitmap(selectedImageBitmap);
                    }
                }
            });
        });
        DatabaseConnection.setLocations(requireContext(), android.R.layout.simple_spinner_dropdown_item,productlocation);
        spinneractivity();
        addspinneractivity();
        productsell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title=productname.getText().toString();
                Bitmap bitmap = ((BitmapDrawable) productimage.getDrawable()).getBitmap();
                String image= ConvertImage.getBase64String(bitmap);
                int time=0;
                int stock=Integer.parseInt(productstock.getText().toString());
                if(!productrenttime.getText().toString().isEmpty()){
                    time=Integer.parseInt(productrenttime.getText().toString());
                    productDuration.setOnItemSelectedListener(listener);
                }
                int amount=Integer.parseInt(productamount.getText().toString());
                if(title.isEmpty()||image.isEmpty()||productstock.getText().toString().isEmpty()){
                    Toast.makeText(view.getContext(), "Please fill missing value", Toast.LENGTH_SHORT).show();
                    return;
                }
                getfeatures();
                Map<String,Object>model1=new HashMap<>();
                model1.put("name",title);
                model1.put("image",image);
                model1.put("time",time);
                model1.put("amount",amount);
                model1.put("feature",features);
                model1.put("rating",0.0);
                model1.put("userid",userid);
                model1.put("location",location);
                model1.put("buyorrent",buyorent);
                model1.put("deliverymode",deliverymode);
                model1.put("measure",measure);
                model1.put("stock",stock);
                model1.put("duration",duration);
                model1.put("date",new Timestamp(new Date()));
                model1.put("category",category);
                db.collection("product")
                        .add(model1)
                        .addOnSuccessListener(DocumentReference->{
                            saveinselling(DocumentReference.getId());
                            Toast.makeText(requireContext(), "Published Successfully", Toast.LENGTH_SHORT).show();
                            getParentFragmentManager().beginTransaction()
                                    .replace(R.id.frameLayout,new SellFragment())
                                    .commit();
                        })
                        .addOnFailureListener(e->{
                            Toast.makeText(requireContext(), "Unsuccessful", Toast.LENGTH_SHORT).show();
                        });
            }
        });
        addfeatures.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.baseline_add_24));
        addfeatures.setImageTintList(null);
        addfeatures.setImageTintList(ContextCompat.getColorStateList(view.getContext(), R.color.white));
        addfeatures.setOnClickListener(v->{
                EditText feature = new EditText(requireContext().getApplicationContext());
                feature.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                featureslist.addView(feature);
        });
        return view;
    }
    public void init(View view){
        productname=view.findViewById(R.id.Create_ProductTitle);
        productimage=view.findViewById(R.id.Create_ProductImage);
        productlocation=view.findViewById(R.id.Create_ProductLocation);
        productamount=view.findViewById(R.id.Create_ProductPrice);
        productbuyorrent=view.findViewById(R.id.Create_ProductBuyOrRent);
        productsell=view.findViewById(R.id.Create_SellProduct);
        addfeatures=view.findViewById(R.id.Create_AddFeatures);
        productdeliverymode=view.findViewById(R.id.Create_ProductDeliveryMethod);
        productstock=view.findViewById(R.id.Create_ProductStock);
        productmeasure=view.findViewById(R.id.Create_ProductMeasure);
        productrenttime=view.findViewById(R.id.Create_ProductRentTime);
        durationLabel=view.findViewById(R.id.Create_ProductRentTimeLabel);
        productDuration=view.findViewById(R.id.Create_ProductRentDuration);
        productCategory=view.findViewById(R.id.Create_ProductCategory);
        featureslist=view.findViewById(R.id.Create_ProductFeatures);
        features=new ArrayList<>();
        userid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        db=FirebaseFirestore.getInstance();
    }
    public void getfeatures(){
        features.clear();
        for(int i=0;i<featureslist.getChildCount();i++){
            View view=featureslist.getChildAt(i);
            if(view instanceof EditText){
                EditText editText=(EditText) view;
                features.add(editText.getText().toString());
            }
        }
    }
    public void saveinselling(String id){
        Map<String,Object> product=new HashMap<>();
        product.put("productid",id);
        product.put("name",productname.getText().toString());
        product.put("date", new Timestamp(new Date()));
        db.collection("users")
                .document(userid)
                .collection("Selling")
                .document(id)
                .set(product)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Database","Added");
                })
                .addOnFailureListener(e -> {
                    Log.d("Database","Unsuccessful");
                });
    }
    public void spinneractivity(){
        listener=new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int spinnerid= parent.getId();
                if(spinnerid==R.id.Create_ProductBuyOrRent){
                    buyorent=parent.getItemAtPosition(position).toString();
                    if(buyorent.equals("Rent")){
                        addvisibility(View.VISIBLE);
                    } else{
                        addvisibility(View.GONE);
                    }
                }
                if(spinnerid==R.id.Create_ProductLocation){
                    location=parent.getItemAtPosition(position).toString();
                }
                if(spinnerid==R.id.Create_ProductDeliveryMethod){
                    deliverymode=parent.getItemAtPosition(position).toString();
                } if(spinnerid==R.id.Create_ProductMeasure){
                    measure=parent.getItemAtPosition(position).toString();
                }
                if(spinnerid==R.id.Create_ProductRentDuration){
                    duration=parent.getItemAtPosition(position).toString();
                }
                if(spinnerid==R.id.Create_ProductCategory){
                    category=parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }
    public void addspinneractivity(){
        productlocation.setOnItemSelectedListener(listener);
        productmeasure.setOnItemSelectedListener(listener);
        productdeliverymode.setOnItemSelectedListener(listener);
        productbuyorrent.setOnItemSelectedListener(listener);
        productDuration.setOnItemSelectedListener(listener);
    }
    public void addvisibility(int visible){
        durationLabel.setVisibility(visible);
        productrenttime.setVisibility(visible);
        productDuration.setVisibility(visible);
    }
}
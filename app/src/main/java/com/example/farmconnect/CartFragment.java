package com.example.farmconnect;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmconnect.Adaptor.Product_Card_Another_Adaptor;
import com.example.farmconnect.ExtraClasses.NavigatetoFragments;
import com.example.farmconnect.Model.Product_Data_Another_Model;
import com.example.farmconnect.Model.Product_Data_Model;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CartFragment extends Fragment implements View.OnClickListener {
    TextView pagetitle,totalprice;
    RecyclerView productView;
    Boolean paid;
    int price=0;
    View chooselocation,debitcard,upi;
    Button buyorrentproducts,confirmbuy,previousproducts,currentproducts;
    LinearLayout paymentview;
    Spinner locationList;
    String deliverylocation,paymentmethod;
    ArrayAdapter adapter;
    ArrayList<Product_Data_Another_Model> productlist;
    Product_Card_Another_Adaptor productCardAnotherAdaptor;
    private FirebaseFirestore db;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_cart, container, false);
        init(view);
        productView.setLayoutManager(new LinearLayoutManager(requireContext()));
        productView.setAdapter(productCardAnotherAdaptor);
        getorderlist();
        setClickListeners();
        return view;
    }
    public void init(View view){
        pagetitle=view.findViewById(R.id.Cart_Title);
        totalprice=view.findViewById(R.id.Cart_TotalPrice);
        productView=view.findViewById(R.id.Cart_ProductList);
        buyorrentproducts=view.findViewById(R.id.Cart_BuyorRentItems);
        confirmbuy=view.findViewById(R.id.BuyProduct_Confirm);
        paymentview=view.findViewById(R.id.Cart_Paymentmethod);
        previousproducts=view.findViewById(R.id.Cart_PreviousOrders);
        currentproducts=view.findViewById(R.id.Cart_CurrentOrders);
        locationList =view.findViewById(R.id.BuyProduct_Location);
        chooselocation=view.findViewById(R.id.BuyProduct_PayOnDelivery);
        debitcard=view.findViewById(R.id.BuyProduct_DebitCard);
        upi=view.findViewById(R.id.BuyProduct_ClickUPI);
        productlist=new ArrayList<>();
        productCardAnotherAdaptor =new Product_Card_Another_Adaptor(productlist,requireContext(),false,false,false);
        db=FirebaseFirestore.getInstance();
    }
    public void setClickListeners(){
        confirmbuy.setOnClickListener(this);
        previousproducts.setOnClickListener(this);
        currentproducts.setOnClickListener(this);
        confirmbuy.setOnClickListener(this);
        buyorrentproducts.setOnClickListener(this);
        chooselocation.setOnClickListener(this);
        debitcard.setOnClickListener(this);
        upi.setOnClickListener(this);
    }
    public void settingadaptor(int look){
        db.collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        DocumentSnapshot documentSnapshot= task.getResult();
                        if(documentSnapshot!=null){
                            ArrayList<String> locationlist=(ArrayList<String>) documentSnapshot.get("location");
                            if(!locationlist.isEmpty()){
                                adapter=new ArrayAdapter<>(requireContext(), look,locationlist);
                                locationList.setAdapter(adapter);
                            }
                        }
                    }
                });
    }
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.Cart_BuyorRentItems){
            paymentview.setVisibility(View.VISIBLE);
            settingadaptor(android.R.layout.simple_spinner_dropdown_item);
            locationList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    deliverylocation=parent.getItemAtPosition(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
        if(v.getId()==R.id.Cart_PreviousOrders){
            pagetitle.setText("Previous Orders");
            buyorrentproducts.setVisibility(View.GONE);
            productlist.clear();
            productCardAnotherAdaptor =new Product_Card_Another_Adaptor(productlist,requireContext(),true,false,true);
            db.collection("users")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .collection("Bought")
                    .addSnapshotListener(((value, error) -> {
                        if(error!=null){
                            Toast.makeText(requireContext(), "Was not able to get orders", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(!value.isEmpty()){
                            int price=0;
                            for(DocumentChange dc: Objects.requireNonNull(value).getDocumentChanges()){
                                if(dc.getType()==DocumentChange.Type.ADDED){
                                    if(dc.getDocument().getId().equals("placeholder")){
                                        continue;
                                    }
                                    Product_Data_Another_Model productDataAnotherModel=dc.getDocument().toObject(Product_Data_Another_Model.class);
                                    productDataAnotherModel.setDocumentid(dc.getDocument().getId());
                                    price+= productDataAnotherModel.getBoughtprice();
                                    productlist.add(productDataAnotherModel);
                                }
                                productView.setAdapter(productCardAnotherAdaptor);
                                totalprice.setText(String.valueOf(price));
                            }
                        }
                    }));
        }
        if(v.getId()==R.id.Cart_CurrentOrders){
            pagetitle.setText("Current Orders");
            buyorrentproducts.setVisibility(View.VISIBLE);
            getorderlist();
        }
        if(v.getId()==R.id.Cart_BuyorRentItems){
            paymentview.setVisibility(View.VISIBLE);
        }
        if(v.getId()==R.id.BuyProduct_ClickUPI){
            paymentmethod="UPI";
            paid=true;
            upi.setBackgroundColor(Color.parseColor("#80FF0000"));
            debitcard.setBackgroundColor(Color.argb(0, 0, 0, 0));
            chooselocation.setBackgroundColor(Color.argb(0, 0, 0, 0));
        }
        if (v.getId()==R.id.BuyProduct_PayOnDelivery){
            paymentmethod="On Delivery";
            paid=false;
            upi.setBackgroundColor(Color.argb(0, 0, 0, 0));
            chooselocation.setBackgroundColor(Color.parseColor("#80FF0000"));
            debitcard.setBackgroundColor(Color.argb(0, 0, 0, 0));
        }
        if(v.getId()==R.id.BuyProduct_DebitCard){
            paymentmethod="Debit Card";
            paid=true;
            upi.setBackgroundColor(Color.argb(0, 0, 0, 0));
            chooselocation.setBackgroundColor(Color.argb(0, 0, 0, 0));
            debitcard.setBackgroundColor(Color.parseColor("#80FF0000"));
        }
        if(v.getId()==R.id.BuyProduct_Confirm){
            db.collection("users")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .collection("Buying")
                    .get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            for(DocumentSnapshot documentSnapshot:task.getResult()){
                                    if(documentSnapshot.getId().equals("placeholder")){
                                        continue;
                                    }
                                    Product_Data_Another_Model model=new Product_Data_Another_Model(documentSnapshot);
                                    model.setPaid(paid);
                                    model.setLocation(deliverylocation);
                                    model.setSoldAt(new Date());
                                    model.setPaymentmethod(paymentmethod);
                                    db.collection("users")
                                            .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .collection("Bought")
                                            .add(model.toMap())
                                            .addOnSuccessListener(task2->{
                                                removefrombuying(documentSnapshot.getId());
                                                decreasestock(model.getProductid(), model.getBoughtquantity());
                                                Toast.makeText(requireContext(), "Successful", Toast.LENGTH_SHORT).show();
                                            });
                                }
                        }
                    });
        }
    }
    public void getorderlist(){
        productlist.clear();
        productView.setLayoutManager(new LinearLayoutManager(requireContext()));
        productCardAnotherAdaptor =new Product_Card_Another_Adaptor(productlist,requireContext(),false,false,false);
        db.collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("Buying")
                .addSnapshotListener(((value, error) -> {
                    if(error!=null){
                        Toast.makeText(requireContext(), "Was not able to get orders", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(!value.isEmpty()){
                        int price=0;
                        for(DocumentSnapshot documentSnapshot:value.getDocuments()){
                            if(documentSnapshot.getId().equals("placeholder")){
                                continue;
                            }
                            Product_Data_Another_Model model=documentSnapshot.toObject(Product_Data_Another_Model.class);
                            model.setDocumentid(documentSnapshot.getId());
                            price+= model.getBoughtprice();
                            productlist.add(model);
                        }
                        productView.setAdapter(productCardAnotherAdaptor);
                        totalprice.setText(String.valueOf(price));
                    }
                        }));
    }
    public void removefrombuying(String id){
        db.collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("Buying")
                .document(id)
                .delete()
                .addOnSuccessListener(task->{
                    Toast.makeText(requireContext(), "Removed successfully from database", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e->{
                    Toast.makeText(requireContext(), "Unable to remove", Toast.LENGTH_SHORT).show();
                });
    }
    public void decreasestock(String id,int quantity){
        DocumentReference reference=db.collection("product").document(id);
        reference.get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()&&task.getResult()!=null){
                        DocumentSnapshot documentSnapshot= task.getResult();
                        int stock= documentSnapshot.getLong("stock").intValue()- quantity;
                        reference.update("stock",stock)
                                .addOnSuccessListener(task2->{
                                    Toast.makeText(requireContext(), "Updated stock successful", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e->{
                                    Toast.makeText(requireContext(), "Could not update stocks", Toast.LENGTH_SHORT).show();
                                });
                    }
                });
    }
}
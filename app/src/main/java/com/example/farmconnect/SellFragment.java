package com.example.farmconnect;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.farmconnect.Adaptor.Product_Card_Adaptor;
import com.example.farmconnect.Adaptor.Product_Card_Another_Adaptor;
import com.example.farmconnect.ExtraClasses.DatabaseConnection;
import com.example.farmconnect.ExtraClasses.NavigatetoFragments;
import com.example.farmconnect.Model.Product_Data_Another_Model;
import com.example.farmconnect.Model.Product_Data_Model;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SellFragment extends Fragment implements Product_Card_Adaptor.onCardListener {
    RecyclerView products;
    Button createproduct,sellingproductbutton,soldproductbutton;
    ArrayList<Product_Data_Model> productmodel;
    Product_Card_Adaptor adaptor;
    private FirebaseFirestore db;
    private String user;
    TextView noproducts;
    public SellFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_sell, container, false);
        init(view);
        createproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment=new CreateProductFragment();
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout,fragment)
                        .commit();
            }
        });
        callingrecycleview("Selling");
        sellingproductbutton.setOnClickListener(v->callingrecycleview("Selling"));
        soldproductbutton.setOnClickListener(v->{
            ArrayList<Product_Data_Another_Model> productDataAnotherModels=new ArrayList<>();
            products.setLayoutManager(new LinearLayoutManager(requireContext()));
            Product_Card_Another_Adaptor adaptor1=new Product_Card_Another_Adaptor(productDataAnotherModels,requireContext(),true,true,false);
            products.setAdapter(adaptor1);
            db.collection("users")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .collection("Sold")
                    .addSnapshotListener(((value, error) -> {
                        if(error!=null){
                            Toast.makeText(requireContext(), "Was not able to get orders", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(!value.isEmpty()){
                            for(DocumentSnapshot documentSnapshot:value.getDocuments()){
                                if(documentSnapshot.getId().equals("placeholder")){
                                    continue;
                                }
                                Product_Data_Another_Model productDataAnotherModel=documentSnapshot.toObject(Product_Data_Another_Model.class);
                                productDataAnotherModel.setDocumentid(documentSnapshot.getId());
                                productDataAnotherModels.add(productDataAnotherModel);
                            }
                            adaptor1.notifyDataSetChanged();
                        }
                    }));
        });
        return view;
    }
    public void init(View view){
        createproduct=view.findViewById(R.id.Sell_CreateProduct_Button);
        products=view.findViewById(R.id.Sell_ProductList);
        noproducts=view.findViewById(R.id.Sell_NoProducts);
        sellingproductbutton=view.findViewById(R.id.Sell_SellingProducts_Button);
        soldproductbutton=view.findViewById(R.id.Sell_SoldOrders_Button);
        productmodel=new ArrayList<>();
        user= FirebaseAuth.getInstance().getCurrentUser().getUid();
        db=FirebaseFirestore.getInstance();
    }
    public void products_list(String collection){
        db.collection("users")
                .document(user)
                .collection(collection)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        ArrayList<String> docuemntid=new ArrayList<>();
                        for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                            if(documentSnapshot.getId().equals("placeholder")){
                                continue;
                            }
                            docuemntid.add(documentSnapshot.getId());
                        }
                        getProducts(docuemntid);
                    }
                });
    }
    public void getProducts(ArrayList<String> ids){
        if(!ids.isEmpty()) {
            DatabaseConnection.getItems( db.collection("product").whereIn(FieldPath.documentId(), ids),adaptor,productmodel,"No",requireContext(),false);
            RecyclerView.Adapter<?> adapter=products.getAdapter();
            if(adapter!=null&&adapter.getItemCount()!=0){
                noproducts.setVisibility(View.VISIBLE);
            } else {
                noproducts.setVisibility(View.GONE);
            }
        }
    }
    public void callingrecycleview(String collection){
        productmodel=new ArrayList<>();
        products.setLayoutManager(new GridLayoutManager(requireContext(),2));
        adaptor=new Product_Card_Adaptor(productmodel,requireContext().getApplicationContext(),this,false);
        products.setAdapter(adaptor);
        products_list(collection);
    }

    @Override
    public void onCardClick(Product_Data_Model model) {
        NavigatetoFragments.navigateToItemDetailFragment(getParentFragmentManager(),model);
    }
}
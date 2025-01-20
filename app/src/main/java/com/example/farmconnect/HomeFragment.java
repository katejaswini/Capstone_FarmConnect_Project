package com.example.farmconnect;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmconnect.Adaptor.Product_Card_Adaptor;
import com.example.farmconnect.ExtraClasses.DatabaseConnection;
import com.example.farmconnect.ExtraClasses.NavigatetoFragments;
import com.example.farmconnect.Model.Product_Data_Model;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements Product_Card_Adaptor.onCardListener {
    RecyclerView recommendation,newlyarrived,toprecommendation;
    ArrayList<Product_Data_Model> productlist,newlyarrivedlist,toprated;
    Product_Card_Adaptor adaptor,newlyarrivedadapter,topratedadapter;
    private FirebaseFirestore db;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_home, container, false);
        recommendation=view.findViewById(R.id.Home_Recommendation);
        newlyarrived=view.findViewById(R.id.Home_NewItems);
        toprecommendation=view.findViewById(R.id.Home_TopRated);
        db=FirebaseFirestore.getInstance();
        setRecycleviews(adaptor,recommendation,productlist,db.collection("product"));
        setRecycleviews(newlyarrivedadapter,newlyarrived,newlyarrivedlist,db.collection("product").orderBy("date"));
        setRecycleviews(topratedadapter,toprecommendation,toprated,db.collection("product").orderBy("rating", Query.Direction.DESCENDING));
        return view;
    }

    @Override
    public void onCardClick(Product_Data_Model model) {
        NavigatetoFragments.navigateToItemDetailFragment(getParentFragmentManager(),model);
    }
    public void setRecycleviews(Product_Card_Adaptor productCardAdaptor,RecyclerView recyclerView,ArrayList<Product_Data_Model> products,Query query){
        products=new ArrayList<>();
        productCardAdaptor=new Product_Card_Adaptor(products,requireContext(),this,true);
        recyclerView.setAdapter(productCardAdaptor);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false));
        DatabaseConnection.getLocation(query,productCardAdaptor,products,requireContext());
    }
}
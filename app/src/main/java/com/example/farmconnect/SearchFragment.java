package com.example.farmconnect;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.farmconnect.Adaptor.Product_Card_Adaptor;
import com.example.farmconnect.Adaptor.Question_Adaptor;
import com.example.farmconnect.ExtraClasses.DatabaseConnection;
import com.example.farmconnect.ExtraClasses.NavigatetoFragments;
import com.example.farmconnect.Model.Comment_Model;
import com.example.farmconnect.Model.Product_Data_Model;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class SearchFragment extends Fragment implements Question_Adaptor.onQuestionClickListener, Product_Card_Adaptor.onCardListener, FilterFragment.SelectedFilters {
    FloatingActionButton createnewquestion;
    RecyclerView recyclerView;
    ArrayList<Comment_Model> questionlist;
    ArrayList<Product_Data_Model> productlist;
    Product_Card_Adaptor productCardAdaptor;
    Question_Adaptor questionAdaptor;
    Spinner selectLocation;
    EditText search;
    String location;
    int first=0;
    private FirebaseFirestore db;
    public SearchFragment() {
        // Required empty public constructor
    }
    public static SearchFragment newInstance(String text){
        SearchFragment fragment=new SearchFragment();
        Bundle args=new Bundle();
        args.putString("page",text);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_search, container, false);
        init(view);
        if(getArguments().getString("page").equals("Search")){
            createnewquestion.setImageResource(R.drawable.filter);
            recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 2));
            productCardAdaptor=new Product_Card_Adaptor(productlist,view.getContext(),this,true);
            recyclerView.setAdapter(productCardAdaptor);
            DatabaseConnection.setLocations(requireContext(), android.R.layout.simple_spinner_dropdown_item,selectLocation);
            selectLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    location=parent.getItemAtPosition(position).toString();
                    if(position!=0&&first==0){
                        DatabaseConnection.getItems(db.collection("product").whereNotEqualTo("userid",FirebaseAuth.getInstance().getCurrentUser().getUid()),productCardAdaptor,productlist,location,requireContext(),true);
                        first++;
                    }
                    if(first!=0){
                        DatabaseConnection.getItems(db.collection("product").whereNotEqualTo("userid",FirebaseAuth.getInstance().getCurrentUser().getUid()),productCardAdaptor,productlist,location,requireContext(),true);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) { }
            });
            createnewquestion.setOnClickListener(v -> {
                FilterFragment fragment=new FilterFragment();
                fragment.setListener(this);
                fragment.show(getParentFragmentManager(),"Filters");
            });
            getproducts();
        } else if(getArguments().getString("page").equals("Query")){
            createnewquestion.setImageTintList(ContextCompat.getColorStateList(view.getContext(), R.color.white));
            selectLocation.setVisibility(View.GONE);
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
            questionAdaptor=new Question_Adaptor(questionlist,view.getContext(),this);
            recyclerView.setAdapter(questionAdaptor);
            getquestions();
            createnewquestion.setOnClickListener(v->{
                    Fragment fragment=new CreateQuestionFragment();
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.frameLayout,fragment)
                            .commit();
            });
        }
        else{
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
            questionAdaptor=new Question_Adaptor(questionlist,view.getContext(),this);
            recyclerView.setAdapter(questionAdaptor);
            gethistory();
        }
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String searchitem=s.toString();
                if(!searchitem.isEmpty()){
                    if(!getArguments().getBoolean("page")){
                        searchitemproduct(searchitem);
                    } else{
                        searchquestion(searchitem);
                    }
                } else {
                    if(!getArguments().getBoolean("page")){
                        getproducts();
                    } else{
                        questionlist.clear();
                        getquestions();
                    }
                }
            }
        });

        return view;
    }
    public void init(View view){
        createnewquestion=view.findViewById(R.id.SearchQuestion_CreatenewQuestion);
        recyclerView=view.findViewById(R.id.Search_List);
        search=view.findViewById(R.id.Search_Searchbar);
        selectLocation=view.findViewById(R.id.Search_Location);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        db=FirebaseFirestore.getInstance();
        questionlist =new ArrayList<>();
        productlist=new ArrayList<>();
    }
    public void getproducts(){
        productlist.clear();
        DatabaseConnection.getLocation(db.collection("product"),productCardAdaptor,productlist,requireContext());
    }
    public void getquestions(){
        questionlist.clear();
        db.collection("question")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()&&task.getResult()!=null){
                        forloopgetdata(task.getResult());
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Could not get Questions", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onQuestionClick(Comment_Model model) {
        QuestionDetailsFragment fragment=QuestionDetailsFragment.newInstance(model);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.frameLayout,fragment)
                .commit();
    }

    @Override
    public void onCardClick(Product_Data_Model model) {
        NavigatetoFragments.navigateToItemDetailFragment(getParentFragmentManager(),model);
    }
    public void searchitemproduct(String text){
        productlist.clear();
        DatabaseConnection.getItems(db.collection("product")
                .orderBy("name")
                .startAt(text)
                .endAt(text+"\uf8ff").whereNotEqualTo("userid", FirebaseAuth.getInstance().getCurrentUser().getUid()),productCardAdaptor,productlist,location,requireContext(),true);

    }
    public void searchquestion(String text){
        questionlist.clear();
        db.collection("question")
                .orderBy("content")
                .whereGreaterThanOrEqualTo("name", text)
                .whereLessThanOrEqualTo("name", text + "\uf8ff")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if(!queryDocumentSnapshots.isEmpty()){
                        forloopgetdata(queryDocumentSnapshots);
                    }else {
                        Toast.makeText(requireContext(), "No question found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error has occurred", Toast.LENGTH_SHORT).show();
                });
    }
    public void forloopgetdata(QuerySnapshot queryDocumentSnapshot){
        for(QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshot){
            Comment_Model model=documentSnapshot.toObject(Comment_Model.class);
            model.setDocumentid(documentSnapshot.getId());
            questionlist.add(model);
        }
        questionAdaptor.notifyDataSetChanged();
    }
    public void gethistory(){
        createnewquestion.setVisibility(View.GONE);
        selectLocation.setVisibility(View.GONE);
        questionlist.clear();
        ArrayList<String> ids=new ArrayList<>();
        db.collection("question")
                        .get()
                                .addOnSuccessListener(command -> {
                                    if(!command.isEmpty()){
                                        for(DocumentSnapshot documentSnapshot:command.getDocuments()){
                                            db.collection("question")
                                                    .document(documentSnapshot.getId())
                                                    .collection("Answer")
                                                    .get()
                                                    .addOnSuccessListener(command1 -> {
                                                        if(!command1.isEmpty()){
                                                            for(DocumentSnapshot documentSnapshot1:command1.getDocuments()){
                                                                String questionid=documentSnapshot1.getString("userid");
                                                                if(questionid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                                                    ids.add(documentSnapshot.getId());
                                                                }
                                                            }
                                                            db.collection("question")
                                                                    .orderBy("timestamp", Query.Direction.DESCENDING)
                                                                    .whereIn(FieldPath.documentId(),ids)
                                                                    .get()
                                                                    .addOnCompleteListener(task -> {
                                                                        if(task.isSuccessful()&&task.getResult()!=null){
                                                                            forloopgetdata(task.getResult());
                                                                        }
                                                                    }).addOnFailureListener(e -> {
                                                                        Toast.makeText(requireContext(), "Could not get Questions", Toast.LENGTH_SHORT).show();
                                                                    });
                                                        }
                                                    }).addOnFailureListener(e -> {
                                                        Toast.makeText(requireContext(), "Failure", Toast.LENGTH_SHORT).show();
                                                    });
                                        }
                                    }
                                }).addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Could not find Questions", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void OverlaySelectedListener(ArrayList<String> selectedproducts,int progressMax,int distanceMax) {
        productlist.clear();
        productlist=new ArrayList<>();
        productCardAdaptor=new Product_Card_Adaptor(productlist,requireContext(),this,true);
        recyclerView.setAdapter(productCardAdaptor);
        DatabaseConnection.getFilterItems(db.collection("product").whereIn("category",selectedproducts).whereLessThanOrEqualTo("amount",progressMax).whereNotEqualTo("userid",FirebaseAuth.getInstance().getCurrentUser().getUid()),productCardAdaptor,productlist,location,requireContext(),true,distanceMax);
    }
}
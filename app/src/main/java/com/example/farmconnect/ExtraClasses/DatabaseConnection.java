package com.example.farmconnect.ExtraClasses;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.farmconnect.Adaptor.Product_Card_Adaptor;
import com.example.farmconnect.Model.Product_Data_Another_Model;
import com.example.farmconnect.Model.Product_Data_Model;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.List;

public class DatabaseConnection {
    private static FirebaseFirestore db=FirebaseFirestore.getInstance();
    private static String user= FirebaseAuth.getInstance().getCurrentUser().getUid();
    public static void getItems(Query query, Product_Card_Adaptor productCardAdaptor, ArrayList<Product_Data_Model> productlist,String location,Context context,boolean isdistance){
        productlist.clear();
        query
                .addSnapshotListener(((value, error) -> {
            if(error!=null){
                Log.d("Product", error.getMessage());
                return;
            }
            if(!value.isEmpty()){
                for(DocumentChange dc: Objects.requireNonNull(value).getDocumentChanges()){
                    if(dc.getType()==DocumentChange.Type.ADDED){
                        Product_Data_Model model=dc.getDocument().toObject(Product_Data_Model.class);
                        model.setProductid(dc.getDocument().getId());
                        if(isdistance){
                            model.setUserlocation(location);
                            model.setContext(context);
                        }
                        productlist.add(model);
                    }
                    productCardAdaptor.notifyDataSetChanged();
                }
            }
        }));
    }
    public static void setLocations( Context context, int look, Spinner productlocation){
        db.collection("users")
                .document(user)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        DocumentSnapshot documentSnapshot= task.getResult();
                        if(documentSnapshot!=null){
                            ArrayList<String> locationlist=(ArrayList<String>) documentSnapshot.get("location");
                            if(!locationlist.isEmpty()){
                                ArrayAdapter<String> adapter=new ArrayAdapter<>(context, look,locationlist);
                                productlocation.setAdapter(adapter);
                            }
                        }
                    }
                });
    }
    public static void getLocation(Query query,Product_Card_Adaptor adaptor,ArrayList<Product_Data_Model> productlist, Context context){
        db.collection("users")
                .document(user)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()&&task.getResult().exists()){
                        List<String> myArray = (List<String>)  task.getResult().get("location");
                        if (myArray != null && !myArray.isEmpty()) {
                            String location = myArray.get(0);
                            DatabaseConnection.getItems(query.whereNotEqualTo("userid", user),adaptor,productlist,location,context,true);
                        } else {
                            Log.d("Firestore", "Array is empty or not found.");
                        }
                    } else {
                        Log.d("Firestore", "No documents found.");
                    }
                });
    }
    public static void updatedatabase(Product_Data_Another_Model model,Context context,String text,boolean change,String collection){
        db.collection("users")
                .document(user)
                .collection(collection)
                .document(model.getDocumentid())
                .update(text,change)
                .addOnCompleteListener(task -> {
                    db.collection("users")
                            .document(model.getSellerid())
                            .collection("Sold")
                            .whereEqualTo("productid",model.getProductid())
                            .whereEqualTo(text,change)
                            .get()
                            .addOnSuccessListener(command -> {
                                for(DocumentSnapshot documentSnapshot:command.getDocuments()){
                                    documentSnapshot.getReference()
                                            .update(text,change)
                                            .addOnSuccessListener(command1 -> {
                                                Toast.makeText(context, "Updated successfullyl", Toast.LENGTH_SHORT).show();
                                            }).addOnFailureListener(e -> {
                                                Toast.makeText(context, "Error 3", Toast.LENGTH_SHORT).show();
                                            });
                                }
                            }).addOnFailureListener(e -> {
                                Toast.makeText(context, "Error 2", Toast.LENGTH_SHORT).show();
                            });
                }).addOnFailureListener(e -> {
                    Toast.makeText(context, "Error 1", Toast.LENGTH_SHORT).show();
                    Log.d("Product",e.getMessage());
                });
    }
    public static void deleteproduct(String productid,Context context){
        db.collection("product")
                .document(user)
                .collection("Selling")
                .document(productid)
                .delete()
                .addOnSuccessListener(command -> {
                    db.collection("product")
                            .document(productid)
                            .delete()
                            .addOnSuccessListener(command1 -> {
                                Toast.makeText(context, "Deleted product successfully", Toast.LENGTH_SHORT).show();
                            });
                }).addOnFailureListener(e -> {
                    Toast.makeText(context, "Could not be deleted", Toast.LENGTH_SHORT).show();
                });
    }
    public static void getFilterItems(Query query, Product_Card_Adaptor productCardAdaptor, ArrayList<Product_Data_Model> productlist,String location,Context context,boolean isdistance,int distance) {
        productlist.clear();
        query
                .addSnapshotListener(((value, error) -> {
                    if (error != null) {
                        Log.d("Product", error.getMessage());
                        return;
                    }
                    if (!value.isEmpty()) {
                        for (DocumentChange dc : Objects.requireNonNull(value).getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                Product_Data_Model model = dc.getDocument().toObject(Product_Data_Model.class);
                                model.setProductid(dc.getDocument().getId());
                                if (isdistance) {
                                    model.setUserlocation(location);
                                    model.setContext(context);
                                }
                                if(model.getUserlocation()/1000>distance){
                                    continue;
                                }
                                productlist.add(model);
                            }
                            productCardAdaptor.notifyDataSetChanged();
                        }
                    }
                }));
    }
    public static void ratings(String productid,double rating,Context context){
        db.collection("product")
                .document(productid)
                .update("rating",rating)
                .addOnSuccessListener(command -> {
                    Toast.makeText(context, "Rated successfully", Toast.LENGTH_SHORT).show();
                });
    }
    public static void addinteractions(String id,int price,String category){
        Map<String,Object> products=new HashMap<>();
        products.put("productid",id);
        products.put("price",price);
        products.put("category",category);
        products.put("timestamp",new Timestamp(new Date()));
        db.collection("users")
                .document(user)
                .collection("Interaction")
                .document(id)
                .set(products)
                .addOnSuccessListener(command -> {
                    Log.d("Interaction","Added");
                }).addOnFailureListener(e -> {
                    Log.d("Interaction","Not added");
                });
    }
    public static void getRecommendations(ArrayList<Product_Data_Model> productlist,Product_Card_Adaptor adaptor,Context context){
        productlist.clear();
        Map<String, Integer> categoryCount = new HashMap<>();
        CollectionReference userpreference= db.collection("users")
                .document(user)
                .collection("Interaction");
        userpreference
                .orderBy("price")
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()&&task.getResult()!=null &&!task.getResult().isEmpty()){
                        DocumentSnapshot documentSnapshot=task.getResult().getDocuments().get(0);
                        int price=documentSnapshot.getDouble("price").intValue();
                        userpreference.get()
                                        .addOnSuccessListener(command -> {
                                            for(DocumentSnapshot documentSnapshot1:command.getDocuments()){
                                                categoryCount.put(documentSnapshot1.getString("category"), categoryCount.getOrDefault(documentSnapshot1.getString("category"), 0) + 1);
                                            }
                                            String category=null;
                                            int maxno=0;
                                            for(Map.Entry<String, Integer>entry:categoryCount.entrySet()){
                                                if(entry.getValue()>maxno){
                                                    maxno= entry.getValue();
                                                    category=entry.getKey();
                                                }
                                            }
                                            getLocation(db.collection("product").orderBy("price").whereEqualTo("category",category).whereNotEqualTo("userid",user).whereLessThanOrEqualTo("price",price),adaptor,productlist,context);
                                        });
                    } else {
                        getLocation(db.collection("product").orderBy("rating", Query.Direction.DESCENDING),adaptor,productlist,context);
                    }
                });
    }
}

package com.example.farmconnect;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.farmconnect.Adaptor.Notification_Adapter;
import com.example.farmconnect.Model.Notification_Model;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class NotificationFragment extends Fragment {
    RecyclerView notifications;
    Notification_Adapter notificationAdapter;
    ArrayList<Notification_Model> notificationModels;
    public NotificationFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_notification, container, false);
        notifications=view.findViewById(R.id.notificationlist);
        notifications.setLayoutManager(new LinearLayoutManager(view.getContext()));
        notificationModels=new ArrayList<>();
        notificationAdapter=new Notification_Adapter(notificationModels,view.getContext());
        notifications.setAdapter(notificationAdapter);
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("Notification")
                .addSnapshotListener(((value, error) -> {
                    if(error!=null){
                        Log.d("NotificationList","Cannot be seen");
                        return;
                    }
                    if(!value.isEmpty()){
                        notificationModels.clear();
                        for(DocumentSnapshot documentSnapshot:value.getDocuments()){
                            if(Boolean.FALSE.equals(documentSnapshot.getBoolean("seen"))){
                                Notification_Model model=documentSnapshot.toObject(Notification_Model.class);
                                model.setDocumentid(documentSnapshot.getId());
                                notificationModels.add(model);
                            }
                        }
                        notificationAdapter.notifyDataSetChanged();
                    }
                }));
        return view;
    }
}
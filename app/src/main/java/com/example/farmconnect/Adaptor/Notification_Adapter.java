package com.example.farmconnect.Adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmconnect.Model.Notification_Model;
import com.example.farmconnect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class Notification_Adapter extends RecyclerView.Adapter<Notification_Adapter.viewHolder> {
    ArrayList<Notification_Model> model;
    Context context;

    public Notification_Adapter(ArrayList<Notification_Model> model, Context context) {
        this.model = model;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_view,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Notification_Model notificationModel=model.get(position);
        holder.notificationtitle.setText(notificationModel.getTitle());
        holder.notificationcontent.setText(notificationModel.getMessage());
        holder.itemView.setOnClickListener(v -> {
            FirebaseFirestore db=FirebaseFirestore.getInstance();
            db.collection("users")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .collection("Notification")
                    .document(notificationModel.getDocumentid())
                    .update("seen",true)
                    .addOnSuccessListener(command -> {
                        Toast.makeText(context, "Seen notification", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        TextView notificationtitle,notificationcontent;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            notificationtitle=itemView.findViewById(R.id.Notification_Title);
            notificationcontent=itemView.findViewById(R.id.Notification_Content);
        }
    }
}

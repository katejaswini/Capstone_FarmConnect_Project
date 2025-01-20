package com.example.farmconnect.Adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmconnect.Model.Comment_Model;
import com.example.farmconnect.R;

import java.util.ArrayList;

public class Comment_Adaptor extends RecyclerView.Adapter<Comment_Adaptor.viewHolder>{
    ArrayList<Comment_Model> comments;
    Context context;

    public Comment_Adaptor(ArrayList<Comment_Model> comments, Context context) {
        this.comments = comments;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.comment_view,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Comment_Model model=comments.get(position);
        holder.name.setText(model.getName());
        holder.comment.setText(model.getContent());
        holder.image.setImageBitmap(model.getImage());
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        TextView name,comment;
        ImageView image;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.Comment_ProfileName);
            comment=itemView.findViewById(R.id.Comment_UserComment);
            image=itemView.findViewById(R.id.Comment_ProfileImage);
        }
    }
}

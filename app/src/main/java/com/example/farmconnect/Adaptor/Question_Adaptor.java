package com.example.farmconnect.Adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmconnect.Model.Comment_Model;
import com.example.farmconnect.R;

import java.util.ArrayList;

public class Question_Adaptor extends RecyclerView.Adapter<Question_Adaptor.viewHolder>{
    ArrayList<Comment_Model> questionList;
    Context context;
    onQuestionClickListener listener;

    public Question_Adaptor(ArrayList<Comment_Model> questionList, Context context, onQuestionClickListener listener) {
        this.questionList = questionList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.question_view,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Comment_Model model=questionList.get(position);
        holder.image.setImageBitmap(model.getImage());
        holder.question.setText(model.getContent());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onQuestionClick(model);
            }
        });
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        TextView question;
        ImageView image;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            question=itemView.findViewById(R.id.Question_PersonQuestion);
            image=itemView.findViewById(R.id.Question_ProfileImage);
        }
    }
    public interface onQuestionClickListener{
        public void onQuestionClick(Comment_Model model);
    }
}

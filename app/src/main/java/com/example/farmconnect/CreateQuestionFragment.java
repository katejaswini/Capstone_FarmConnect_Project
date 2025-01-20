package com.example.farmconnect;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.farmconnect.Model.Comment_Model;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CreateQuestionFragment extends Fragment{
    TextView createquestion;
    Button addquestion;
    String user,image,name;
    private FirebaseFirestore db;
    public CreateQuestionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_create_question, container, false);
        createquestion=view.findViewById(R.id.CreateQuestion_EnterQuestion);
        addquestion=view.findViewById(R.id.CreateQuestion_AskQuestion);
        user=FirebaseAuth.getInstance().getCurrentUser().getUid();
        db=FirebaseFirestore.getInstance();
        addquestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String question=createquestion.getText().toString();
                if(question.isEmpty()){
                    Toast.makeText(requireContext(), "Enter your question", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    db.collection("users")
                            .document(user)
                            .get()
                            .addOnCompleteListener(task -> {
                                if(task.isSuccessful()){
                                    DocumentSnapshot documentSnapshot=task.getResult();
                                    if(documentSnapshot!=null){
                                        image=documentSnapshot.getString("image");
                                        name=documentSnapshot.getString("name");
                                        Map<String,Object> model=new HashMap<>();
                                        model.put("name",name);
                                        model.put("image",image);
                                        model.put("content",question);
                                        model.put("timestamp",new Timestamp(new Date()));
                                        model.put("userid",user);
                                        addtodatabase(model);
                                    }
                                }
                            });
                }
            }
        });
        return view;
    }
    public void addtodatabase(Map model){
        db.collection("question")
                .add(model)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(requireContext(), "Question Posted Successfully", Toast.LENGTH_SHORT).show();
                    createquestion.setText("");
                })
                .addOnFailureListener(e->{
                    Toast.makeText(requireContext(), "Failed to upload", Toast.LENGTH_SHORT).show();
                });
    }
}
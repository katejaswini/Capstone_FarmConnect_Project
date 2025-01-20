package com.example.farmconnect;

import static com.example.farmconnect.ExtraClasses.AddComments.getcomments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.farmconnect.Adaptor.Comment_Adaptor;
import com.example.farmconnect.ExtraClasses.AddComments;
import com.example.farmconnect.ExtraClasses.ConvertImage;
import com.example.farmconnect.Model.Comment_Model;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class QuestionDetailsFragment extends Fragment {
    TextView content;
    ImageView image;
    EditText comment;
    ImageView sendcomment;
    String username,userimage,userid;
    RecyclerView commentlist;
    ArrayList<Comment_Model> commentModel;
    Comment_Adaptor adaptor;
    private FirebaseFirestore db;
    public static QuestionDetailsFragment newInstance(Comment_Model model){
        QuestionDetailsFragment fragment=new QuestionDetailsFragment();
        Bundle args=new Bundle();
        args.putString("name",model.getName());
        args.putString("image", ConvertImage.getBase64String(model.getImage()));
        args.putString("content", model.getContent());
        args.putString("documentid", model.getDocumentid());
        args.putString("userid", model.getUserid());
        fragment.setArguments(args);
        return fragment;
    }
    public QuestionDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_question_details, container, false);
        init(view);
        content.setText(getArguments().getString("content"));
        image.setImageBitmap(ConvertImage.getBitmap(getArguments().getString("image")));
        AddComments.init(view,getArguments().getString("documentid"),view.getContext(),"question","Answer",getArguments().getString("content"),getArguments().getString("userid"));
        getcomments();
        return view;
    }
    public void init(View view){
        content=view.findViewById(R.id.Question_PersonQuestion);
        image=view.findViewById(R.id.Question_ProfileImage);
    }
}
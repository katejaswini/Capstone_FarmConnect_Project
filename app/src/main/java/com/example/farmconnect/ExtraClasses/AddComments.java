package com.example.farmconnect.ExtraClasses;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmconnect.Adaptor.Comment_Adaptor;
import com.example.farmconnect.Model.Comment_Model;
import com.example.farmconnect.Model.Notification_Model;
import com.example.farmconnect.R;
import com.example.farmconnect.SearchFragment;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddComments {
    static Context context;
    static EditText comment;
    static RecyclerView commentlist;
    static ArrayList<Comment_Model> commentModel;
    static ImageView sendcomment;
    static Comment_Adaptor adaptor;
    static String username,userimage,userid,documentid,collectiontype,queryornot,objectname,notifyuser;
    private static FirebaseFirestore db;
    public static void init(View view, String document, Context fragment,String collection,String type,String itemname,String userids){
        sendcomment=view.findViewById(R.id.QuestionDetails_SendComment);
        comment=view.findViewById(R.id.QuestionDetails_Comment);
        commentlist=view.findViewById(R.id.QuestionDetails_CommentList);
        userid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        notifyuser=userids;
        db= FirebaseFirestore.getInstance();
        commentModel=new ArrayList<>();
        documentid=document;
        context=fragment;
        collectiontype=collection;
        queryornot=type;
        objectname=itemname;
        clickaction();
    }
    public static void clickaction(){
        sendcomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reply=comment.getText().toString();
                if(reply.isEmpty()){
                    Toast.makeText(context, "Please enter your views", Toast.LENGTH_SHORT).show();
                    return;
                } else{
                    db.collection("users")
                            .document(userid)
                            .get()
                            .addOnCompleteListener(task -> {
                                if(task.isSuccessful() && task.getResult()!=null){
                                    DocumentSnapshot documentSnapshot=task.getResult();
                                    username=documentSnapshot.getString("name");
                                    userimage=documentSnapshot.getString("image");
                                    comment.setText("");
                                    commentModel.clear();
                                    addcomments(username,userimage,reply,queryornot);
                                }
                            });
                }
            }
        });
    }
    public static void addcomments(String name, String image, String content,String collection){
        Map<String,Object> commentdetail=new HashMap<>();
        commentdetail.put("name",name);
        commentdetail.put("image",image);
        commentdetail.put("content",content);
        commentdetail.put("userid",userid);
        commentdetail.put("timestamp",new Timestamp(new Date()));
        db.collection(collectiontype)
                .document(documentid)
                .collection(collection)
                .add(commentdetail)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(context, "Commented successfully", Toast.LENGTH_SHORT).show();
                    if(!documentid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        if(queryornot.equals("Answer")){
                            getnotifications("Query has been answered by "+name,objectname);
                        }else {
                            getnotifications("Product was reviewed",name+" commented on "+objectname);
                        }
                    }
                    getcomments();
                }).addOnFailureListener(e -> {
                    Toast.makeText(context, "Unable to add comment", Toast.LENGTH_SHORT).show();
                });
    }
    public static void getcomments(){
        commentlist.setLayoutManager(new LinearLayoutManager(context));
        adaptor=new Comment_Adaptor(commentModel,context);
        commentlist.setAdapter(adaptor);
        db.collection(collectiontype)
                .document(documentid)
                .collection(queryornot)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()&&task.getResult()!=null){
                        for(QueryDocumentSnapshot documentSnapshot:task.getResult()){
                            Comment_Model model=documentSnapshot.toObject(Comment_Model.class);
                            commentModel.add(model);
                        }
                        adaptor.notifyDataSetChanged();
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(context, "Could not get comments", Toast.LENGTH_SHORT).show();
                });
    }
    public static void getnotifications(String title,String message){
        Notification_Model notificationModel=new Notification_Model(title,message);
        db.collection("users")
                .document(notifyuser)
                .collection("Notification")
                .add(notificationModel.getMapNotification())
                .addOnCompleteListener(task1 -> {
                    Toast.makeText(context, "Updated User successfully", Toast.LENGTH_SHORT).show();

                });
    }
}

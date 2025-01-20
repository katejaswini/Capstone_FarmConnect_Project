package com.example.farmconnect.ExtraClasses;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;
public class TextCheckOperation {
    public static void checkempty(String text, String box, Context context){
        if(TextUtils.isEmpty(text)){
            Toast.makeText(context,box+" is empty",Toast.LENGTH_SHORT).show();
            return;
        }
    }
    public static void checkvalid(String text, TextView textView,int validlenght,String message){
        if(text.length()<validlenght){
            textView.setError(message);
            textView.requestFocus();
            return;
        }
    }
}

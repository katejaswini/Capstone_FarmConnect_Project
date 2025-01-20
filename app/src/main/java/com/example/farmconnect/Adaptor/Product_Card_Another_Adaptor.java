package com.example.farmconnect.Adaptor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmconnect.ExtraClasses.DatabaseConnection;
import com.example.farmconnect.Model.Product_Data_Another_Model;
import com.example.farmconnect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class Product_Card_Another_Adaptor extends RecyclerView.Adapter<Product_Card_Another_Adaptor.viewHolder>{
    ArrayList<Product_Data_Another_Model> productDataAnotherModels;
    Context context;
    Boolean isVisible,ispaid=false,isdelivered=false;

    public Product_Card_Another_Adaptor(ArrayList<Product_Data_Another_Model> productDataAnotherModels, Context context,Boolean isVisible,boolean ispaid,boolean isdelivered) {
        this.productDataAnotherModels = productDataAnotherModels;
        this.context = context;
        this.isVisible=isVisible;
        this.ispaid=ispaid;
        this.isdelivered=isdelivered;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.product_card_another_view,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Product_Data_Another_Model model=productDataAnotherModels.get(position);
        holder.price.setText(String.valueOf(model.getBoughtprice()));
        holder.title.setText(model.getName());
        holder.imageView.setImageBitmap(model.getImage());
        holder.quantity.setText(String.valueOf(model.getBoughtquantity()));
        if(isVisible){
            holder.delete.setVisibility(View.GONE);
        }
        holder.delete.setOnClickListener(v -> {
            productDataAnotherModels.remove(position);
            notifyItemRemoved(position);
            deletefromfirestore(model);
        });
        if(isdelivered) {
            if (!model.isDelivered()) {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
                holder.itemView.setOnClickListener(v -> {
                    createAlertBox("delivered", model, true,"Bought");
                });
            }
        }
        if(ispaid) {
            if (!model.isPaid()) {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
                holder.itemView.setOnClickListener(v -> {
                    createAlertBox("paid", model, true,"Sold");
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return productDataAnotherModels.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        ImageButton delete;
        TextView title,quantity,price;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.ProductCard_Image);
            delete=itemView.findViewById(R.id.ProductCard_Delete);
            title=itemView.findViewById(R.id.ProductCard_Title);
            quantity=itemView.findViewById(R.id.ProductCard_Quantity);
            price=itemView.findViewById(R.id.ProductCard_Price);
        }
    }
    public ArrayList<Product_Data_Another_Model> getModelList(){
        return productDataAnotherModels;
    }
    public void deletefromfirestore(Product_Data_Another_Model model){
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("Buying")
                .document(model.getDocumentid())
                .delete()
                .addOnSuccessListener(task->{
                    Toast.makeText(context, "Successfully deleted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e->{
                    Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show();
                });
    }
    public void createAlertBox(String text,Product_Data_Another_Model model,boolean change,String collection){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirm "+text)
                .setMessage("Please confirm if it is "+text)
                .setPositiveButton(text,((dialog, which) -> {
                    Toast.makeText(context, model.getSellerid(), Toast.LENGTH_SHORT).show();
                    DatabaseConnection.updatedatabase(model,context,text,change,collection);}));
        builder.show();
    }
}

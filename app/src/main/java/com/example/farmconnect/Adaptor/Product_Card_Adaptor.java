package com.example.farmconnect.Adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmconnect.ExtraClasses.DatabaseConnection;
import com.example.farmconnect.Model.Product_Data_Model;
import com.example.farmconnect.R;

import java.util.ArrayList;

public class Product_Card_Adaptor extends RecyclerView.Adapter<Product_Card_Adaptor.viewHolder>{
    ArrayList<Product_Data_Model> productList;
    Context context;
    private onCardListener listener;
    boolean isdistance;

    public Product_Card_Adaptor(ArrayList<Product_Data_Model> model, Context context,onCardListener listener, boolean isdistance) {
        this.productList = model;
        this.context = context;
        this.listener=listener;
        this.isdistance=isdistance;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.product_card_view,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Product_Data_Model model=productList.get(position);
        holder.producttitle.setText(model.getName());
        holder.productimage.setImageBitmap(model.getImage());
        holder.ratings.setText(String.valueOf(model.getRating()));
        if(isdistance){
            holder.distance.setVisibility(View.VISIBLE);
            float distance=model.getUserlocation()/1000;
            if(distance<1){
                holder.distance.setText(String.format("%.01f m",distance));
            } else{
                holder.distance.setText(String.format("%.01f km",distance));
            }

        }
        holder.itemView.setOnClickListener(v->{
            listener.onCardClick(model);
            DatabaseConnection.addinteractions(model.getProductid(),model.getAmount(),model.getCategory());
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        ImageView productimage;
        TextView producttitle,distance,ratings;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            productimage=itemView.findViewById(R.id.Product_Image);
            producttitle=itemView.findViewById(R.id.ProductName);
            distance=itemView.findViewById(R.id.Product_Distance);
            ratings=itemView.findViewById(R.id.Product_Rating);
        }
    }
    public interface onCardListener{
        public void onCardClick(Product_Data_Model model);
    }
}

package com.example.farmconnect;

import static com.example.farmconnect.ExtraClasses.AddComments.getcomments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.example.farmconnect.ExtraClasses.AddComments;
import com.example.farmconnect.ExtraClasses.DatabaseConnection;
import com.example.farmconnect.Model.Product_Data_Model;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ProductDetailsFragment extends Fragment {
    TextView title,location,price,stock,buyorrent,features,featurelabel,measure,deliverymode,duration,durationlabel;
    ImageView image;
    Button buyorrentbutton,addtocartbutton,deletebutton;
    Product_Data_Model product;
    RatingBar ratingBar;
    public static ProductDetailsFragment newInstance(Product_Data_Model model){
        ProductDetailsFragment fragment=new ProductDetailsFragment();
        Bundle args=new Bundle();
        args.putParcelable("model",model);
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_product_details, container, false);
        Toast.makeText(view.getContext(), "ProductDetails", Toast.LENGTH_SHORT).show();
        init(view);
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(product.getUserid())){
            deletebutton.setVisibility(View.VISIBLE);
            view.findViewById(R.id.ProductDetails_CommentArea).setVisibility(View.GONE);
            ratingBar.setVisibility(View.GONE);
            addtocartbutton.setVisibility(View.GONE);
            buyorrentbutton.setVisibility(View.GONE);
        } else{
            deletebutton.setVisibility(View.GONE);
            view.findViewById(R.id.ProductDetails_CommentArea).setVisibility(View.VISIBLE);
            ratingBar.setVisibility(View.VISIBLE);
            addtocartbutton.setVisibility(View.VISIBLE);
            buyorrentbutton.setVisibility(View.VISIBLE);
        }
        setTexts();
        if(product.getTime()!=0){
            durationlabel.setVisibility(View.VISIBLE);
            String rent=product.getTime()+" "+product.getDuration();
            duration.setText(rent);
            duration.setVisibility(View.VISIBLE);
        } else{
            durationlabel.setVisibility(View.GONE);
            duration.setVisibility(View.GONE);
        }
        ArrayList<String> featureList=new ArrayList<>(product.getFeature());
        if(featureList.isEmpty()){
            features.setVisibility(View.GONE);
            featurelabel.setVisibility(View.GONE);
        } else{
            featurelabel.setVisibility(View.VISIBLE);
            String text="<ul>";
            for(int i=0;i<featureList.size();i++){
                text+="<li>"+featureList.get(i)+"</li>";
            }
            text+="</ul>";
            features.setText(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY));
            features.setVisibility(View.VISIBLE);
        }
        buyorrentbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movetobuyproduct(true);
            }
        });
        addtocartbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movetobuyproduct(false);
            }
        });
        deletebutton.setOnClickListener(v->{
            DatabaseConnection.deleteproduct(product.getProductid(),requireContext());
            FragmentTransaction transaction= getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayout,new SellFragment())
                    .commit();
        });
        ratingBar.setOnRatingBarChangeListener(((ratingBar1, rating, fromUser) -> {
            DatabaseConnection.ratings(product.getProductid(),product.getRating()+rating,view.getContext());
        }));
        AddComments.init(view,product.getProductid(), view.getContext(),"product","Reviews", product.getName(),product.getUserid());
        getcomments();
        return view;
    }
    public void init(View view){
        assert getArguments() != null;
        product=getArguments().getParcelable("model");
        title=view.findViewById(R.id.ProductDetails_Title);
        image=view.findViewById(R.id.ProductDetails_Image);
        location=view.findViewById(R.id.ProductDetails_Location);
        price=view.findViewById(R.id.ProductDetails_Price);
        stock=view.findViewById(R.id.ProductDetails_InStock);
        buyorrent=view.findViewById(R.id.ProductDetails_BuyorRent);
        features=view.findViewById(R.id.ProductDetails_Features);
        featurelabel=view.findViewById(R.id.ProductDetails_FeaturesLabel);
        deliverymode=view.findViewById(R.id.ProductDetails_DeliveryMethod);
        measure=view.findViewById(R.id.ProductDetails_Measure);
        duration=view.findViewById(R.id.ProductDetails_Duration);
        durationlabel=view.findViewById(R.id.ProductDetails_DurationLabel);
        buyorrentbutton=view.findViewById(R.id.ProductDetails_BuyRentProduct_Button);
        addtocartbutton=view.findViewById(R.id.ProductDetails_AddtoCart_Button);
        deletebutton=view.findViewById(R.id.ProductDetails_DeleteProduct);
        ratingBar=view.findViewById(R.id.ProductDetails_Rating);
    }
    public void movetobuyproduct(Boolean buyorcart){
        BuyProductFragment fragment=BuyProductFragment.newInstance(product,buyorcart);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.frameLayout,fragment)
                .commit();
    }
    public void setTexts(){
        title.setText(product.getName());
        image.setImageBitmap(product.getImage());
        location.setText(product.getLocation());
        price.setText(String.valueOf(product.getAmount()));
        buyorrent.setText(product.getBuyorrent());
        measure.setText(product.getMeasure());
        deliverymode.setText(product.getDeliverymode());
        stock.setText(String.valueOf(product.getStock()));
    }
}
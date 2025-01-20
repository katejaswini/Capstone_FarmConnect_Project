package com.example.farmconnect;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.farmconnect.ExtraClasses.ConvertImage;
import com.example.farmconnect.ExtraClasses.DatabaseConnection;
import com.example.farmconnect.Model.Notification_Model;
import com.example.farmconnect.Model.Product_Data_Another_Model;
import com.example.farmconnect.Model.Product_Data_Model;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.protobuf.Value;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BuyProductFragment extends Fragment implements View.OnClickListener {
    View chooselocation,debitcard,upi;
    ImageButton add,subtract;
    ImageView image;
    Button buyorrentbutton,addtocartbutton,confirmbuy;
    TextView quantity,price,lastday,lastdaylabel,title;
    String paymentmethod,deliverylocation;
    Timestamp rentduedate;
    Calendar calendar;
    Boolean paid;
    Spinner location;
    ArrayAdapter adapter;
    Map<String,Object> productdatabase;
    Product_Data_Model productDetail;
    Product_Data_Another_Model productmodel;
    LinearLayout paymentmethodlayout;
    private FirebaseFirestore db;
    public BuyProductFragment() {}
    public static BuyProductFragment newInstance(Product_Data_Model model,Boolean buyorcart) {
        BuyProductFragment fragment = new BuyProductFragment();
        Bundle args = new Bundle();
        args.putParcelable("model",model);
        args.putBoolean("buyorcart",buyorcart);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_buy_product, container, false);
        init(view);
        title.setText(productDetail.getName());
        image.setImageBitmap(productDetail.getImage());
        if(getArguments().getBoolean("buyorcart")){
            paymentmethodlayout.setVisibility(View.VISIBLE);
            buyorrentbutton.setVisibility(View.GONE);
            DatabaseConnection.setLocations(requireContext(), android.R.layout.simple_spinner_dropdown_item,location);
            location.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    deliverylocation=parent.getItemAtPosition(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        } else{
            paymentmethodlayout.setVisibility(View.GONE);
            buyorrentbutton.setVisibility(View.VISIBLE);
        }
        addclicklisteners();
        quantity.setText("1");
        price.setText(String.valueOf(1*productDetail.getAmount()));
        if(productDetail.getTime()==0){
            lastday.setVisibility(View.GONE);
            lastdaylabel.setVisibility(View.GONE);
        } else{
            lastday.setVisibility(View.VISIBLE);
            lastdaylabel.setVisibility(View.VISIBLE);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
            String last="";
            if(productDetail.getDuration().equals("Month")){
                calendar.add(calendar.MONTH,productDetail.getTime());
                rentduedate=new Timestamp(calendar.getTime());
                last=String.valueOf(dateFormat.format(calendar.getTime()));
            } else{
               calendar.add(calendar.YEAR,productDetail.getTime());
                rentduedate=new Timestamp(calendar.getTime());
                last=String.valueOf(dateFormat.format(calendar.getTime()));
            }
            lastday.setText(last);
        }

        return view;
    }
    public void init(View view){
        title=view.findViewById(R.id.BuyProduct_ProductTitle);
        image=view.findViewById(R.id.BuyProduct_Image);
        add=view.findViewById(R.id.BuyProduct_AddQuantity);
        subtract=view.findViewById(R.id.BuyProduct_SubtractQuantity);
        quantity=view.findViewById(R.id.BuyProduct_TotalQuantity);
        price=view.findViewById(R.id.BuyProduct_TotalPrice);
        chooselocation=view.findViewById(R.id.BuyProduct_PayOnDelivery);
        debitcard=view.findViewById(R.id.BuyProduct_DebitCard);
        upi=view.findViewById(R.id.BuyProduct_ClickUPI);
        paymentmethodlayout=view.findViewById(R.id.BuyProduct_PaymentMethod);
        lastday=view.findViewById(R.id.BuyProduct_RentTime);
        lastdaylabel=view.findViewById(R.id.BuyProduct_RentTimeLabel);
        buyorrentbutton=view.findViewById(R.id.BuyProduct_BuyorRentButton);
        confirmbuy=view.findViewById(R.id.BuyProduct_Confirm);
        addtocartbutton=view.findViewById(R.id.BuyProduct_AddtoCart);
        location=view.findViewById(R.id.BuyProduct_Location);
        calendar= Calendar.getInstance();
        productDetail=getArguments().getParcelable("model");
        productmodel=new Product_Data_Another_Model();
        productdatabase=new HashMap<>();
        db=FirebaseFirestore.getInstance();
    }
    public void addclicklisteners() {
        add.setOnClickListener(this);
        subtract.setOnClickListener(this);
        chooselocation.setOnClickListener(this);
        debitcard.setOnClickListener(this);
        upi.setOnClickListener(this);
        buyorrentbutton.setOnClickListener(this);
        confirmbuy.setOnClickListener(this);
        addtocartbutton.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        int viewid=v.getId();
        if(viewid==R.id.BuyProduct_ClickUPI){
            paymentmethod="UPI";
            paid=true;
            upi.setBackgroundColor(Color.parseColor("#80FF0000"));
            debitcard.setBackgroundColor(Color.argb(0, 0, 0, 0));
            chooselocation.setBackgroundColor(Color.argb(0, 0, 0, 0));
        }
        if (viewid==R.id.BuyProduct_PayOnDelivery){
            paymentmethod="On Delivery";
            paid=false;
            upi.setBackgroundColor(Color.argb(0, 0, 0, 0));
            chooselocation.setBackgroundColor(Color.parseColor("#80FF0000"));
            debitcard.setBackgroundColor(Color.argb(0, 0, 0, 0));
        }
        if(viewid==R.id.BuyProduct_DebitCard){
            paymentmethod="Debit Card";
            paid=true;
            upi.setBackgroundColor(Color.argb(0, 0, 0, 0));
            chooselocation.setBackgroundColor(Color.argb(0, 0, 0, 0));
            debitcard.setBackgroundColor(Color.parseColor("#80FF0000"));
        }
        if(viewid==R.id.BuyProduct_AddQuantity){
            int totalquantity=Integer.parseInt(quantity.getText().toString());
            int totalprice=Integer.parseInt(price.getText().toString());
            if(totalquantity>productDetail.getStock()){
                Toast.makeText(requireContext(), "No more can be added", Toast.LENGTH_SHORT).show();
                return;
            } else {
                totalquantity+=1;
                totalprice=totalquantity*productDetail.getAmount();
                quantity.setText(String.valueOf(totalquantity));
                price.setText(String.valueOf(totalprice));
            }
        }
        if(viewid==R.id.BuyProduct_SubtractQuantity){
            int totalquantity=Integer.parseInt(quantity.getText().toString());
            int totalprice=Integer.parseInt(price.getText().toString());
            if(totalquantity==1){
                Toast.makeText(requireContext(), "No more can be subtracted", Toast.LENGTH_SHORT).show();
                return;
            } else {
                totalquantity-=1;
                totalprice=totalquantity*productDetail.getAmount();
                quantity.setText(String.valueOf(totalquantity));
                price.setText(String.valueOf(totalprice));
            }
        }
        if(viewid==R.id.BuyProduct_BuyorRentButton){
            paymentmethodlayout.setVisibility(View.VISIBLE);
            buyorrentbutton.setVisibility(View.GONE);
        }
        if(viewid==R.id.BuyProduct_Confirm){
            broughtproduct();
            productmodel.setPaymentmethod(paymentmethod);
            productmodel.setPaid(paid);
            productmodel.setLocation(deliverylocation);
            db.collection("users")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .collection("Bought")
                    .document()
                    .set(productmodel.toMap())
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            Toast.makeText(requireContext(), "Successfully brought", Toast.LENGTH_SHORT).show();
                            updateproductowner(productmodel.toMap());
                            updatedatabase();
                        }
                    }).addOnFailureListener(e->{
                        Toast.makeText(requireContext(), "Failed to add", Toast.LENGTH_SHORT).show();
                    });
        }
        if(viewid==R.id.BuyProduct_AddtoCart){
            broughtproduct();
            db.collection("users")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .collection("Buying")
                    .add(productmodel.toMap())
                    .addOnSuccessListener(task->{
                        Toast.makeText(requireContext(), "Done Successfully", Toast.LENGTH_SHORT).show();
                        openFragment(new CartFragment());
                    });
        }
    }
    public void broughtproduct() {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        productmodel.setImage(ConvertImage.getBase64String(bitmap));
        productmodel.setName(productDetail.getName());
        productmodel.setBoughtprice(Integer.parseInt(price.getText().toString()));
        productmodel.setBoughtquantity(Integer.parseInt(quantity.getText().toString()));
        productmodel.setProductid(productDetail.getProductid());
        productmodel.setSellerid(productDetail.getUserid());
        if(rentduedate!=null){
            productmodel.setDateofreturn(rentduedate.toDate());
        }
    }
    public void updatedatabase(){
        int stock=productDetail.getStock()-Integer.parseInt(quantity.getText().toString());
        if(stock<0){
            stock=0;
        }
        db.collection("product")
                .document(productDetail.getProductid())
                .update("stock",stock)
                .addOnSuccessListener(task->{
                    Toast.makeText(requireContext(), "Successfully updated", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e->{
                    Toast.makeText(requireContext(), "Failed to update", Toast.LENGTH_SHORT).show();
                });
    }
    public void updateproductowner(Map product){
        product.put("consumerid",FirebaseAuth.getInstance().getCurrentUser().getUid());
        product.put("soldAt",new Timestamp(Calendar.getInstance().getTime()));
        db.collection("users")
                .document(productDetail.getUserid())
                .collection("Sold")
                .add(product)
                .addOnSuccessListener(task->{
                    String message="Quantity: "+productmodel.getBoughtquantity()+"Price: "+productmodel.getBoughtprice()+" At "+Calendar.getInstance().getTime();
                    Notification_Model notificationModel=new Notification_Model(productmodel.getName(),message);
                    db.collection("users")
                            .document(productDetail.getUserid())
                            .collection("Notification")
                            .add(notificationModel.getMapNotification())
                            .addOnCompleteListener(task1 -> {
                                Toast.makeText(requireContext(), "Updated User successfully", Toast.LENGTH_SHORT).show();
                                SearchFragment fragment=SearchFragment.newInstance("Search");
                                openFragment(fragment);
                            });
                })
                .addOnFailureListener(e->{
                    Toast.makeText(requireContext(), "Could not update user successfully", Toast.LENGTH_SHORT).show();
                });
    }
    public void openFragment(Fragment fragment){
        getParentFragmentManager().beginTransaction()
                .replace(R.id.frameLayout,fragment)
                .commit();
    }
}
package com.example.farmconnect;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class FilterFragment extends DialogFragment {
    CheckBox seeds,machine,plant,produce;
    SelectedFilters listener;
    SeekBar productprice,productdistance;
    TextView productpricemax,productdistancemax;
    private FirebaseFirestore db;
    public FilterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_filter, container, false);
        init(view);
        db.collection("product")
                        .orderBy("amount", Query.Direction.DESCENDING)
                                .limit(1).get()
                        .addOnSuccessListener(command -> {
                            if(!command.isEmpty()){
                                DocumentSnapshot documentSnapshot=command.getDocuments().get(0);
                                productpricemax.setText(String.valueOf(documentSnapshot.getLong("amount")));
                                productprice.setMax(documentSnapshot.getDouble("amount").intValue());
                                productprice.setProgress(productprice.getMax());
                            }
                        })
                                .addOnFailureListener(e -> {
                                    Log.d("Price","Error");
                                });
        productprice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                productpricemax.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        productdistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                productdistancemax.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        view.findViewById(R.id.Filter_Confirm_Filter).setOnClickListener(v -> {
            ArrayList<String> checkedboxes=new ArrayList<>();
            if(seeds.isChecked())
                checkedboxes.add("Seeds");
            if(machine.isChecked())
                checkedboxes.add("Machine");
            if(plant.isChecked())
                checkedboxes.add("Plant Care");
            if(produce.isChecked())
                checkedboxes.add("Produce");
            if(listener!=null){
                listener.OverlaySelectedListener(checkedboxes,productprice.getProgress(),productdistance.getProgress());
            }
            dismiss();
        });
        return view;
    }

    private void init(View view) {
        seeds=view.findViewById(R.id.Filter_Seeds_Checkbox);
        machine=view.findViewById(R.id.Filter_Machine_CheckBox);
        plant=view.findViewById(R.id.Filter_Plant_Care_CheckBox);
        produce=view.findViewById(R.id.Filter_Produce_Checkbox);
        productprice=view.findViewById(R.id.Filter_ProductPrice_Seekbar);
        productpricemax=view.findViewById(R.id.Filter_ProductPriceMax);
        productdistance=view.findViewById(R.id.Filter_ProductLocation_Seekbar);
        productdistancemax=view.findViewById(R.id.Filter_ProductLocationMax);
        db=FirebaseFirestore.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog=getDialog();
        if(dialog!=null){
            Window window= dialog.getWindow();
            if(window!=null){
                window.setLayout(getResources().getDisplayMetrics().widthPixels * 3 / 4,ViewGroup.LayoutParams.MATCH_PARENT);
                window.setGravity(Gravity.END);
            }
        }
    }
    public void setListener(SelectedFilters listener){
        this.listener=listener;
    }
    public interface SelectedFilters{
        public void OverlaySelectedListener(ArrayList<String> selectedproducts,int progressbar,int distancemax);
    }
}
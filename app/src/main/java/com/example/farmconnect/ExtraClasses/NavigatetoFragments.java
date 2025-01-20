package com.example.farmconnect.ExtraClasses;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.FragmentManager;

import com.example.farmconnect.Model.Product_Data_Model;
import com.example.farmconnect.ProductDetailsFragment;
import com.example.farmconnect.R;

public class NavigatetoFragments {
    public static void navigateToItemDetailFragment(FragmentManager fragmentManager, Product_Data_Model item) {
        ProductDetailsFragment detailFragment = ProductDetailsFragment.newInstance(item);
        fragmentManager.beginTransaction()
                .replace(R.id.frameLayout, detailFragment)
                .addToBackStack(null)
                .commit();
    }
    public static Intent navigatetoPages(Context context, String type, String time, Class movepage){
        Intent intent=new Intent(context, movepage);
        intent.putExtra("userType",type);
        intent.putExtra("Firsttime",time);
        return intent;
    }
}

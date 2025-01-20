package com.example.farmconnect;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.farmconnect.ExtraClasses.ConvertImage;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;


public class MainPage extends AppCompatActivity{
    Toolbar toolbar;
    BottomNavigationView bottomnav;
    FloatingActionButton fab;
    CardView card;
    ImageView profileimage,bellicon;
    TextView bellcount,pagetitle;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        db.collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("Notification")
                .addSnapshotListener((value, error) -> {
                    if(error!=null){
                        Log.d("Notification","Notification Error");
                        return;
                    }
                    int countnotification=0;
                    for(DocumentSnapshot dc:value.getDocuments()){
                        if(!dc.getBoolean("seen")){
                            countnotification++;
                        }
                    }
                    Log.d("Count",String.valueOf(countnotification));
                    updateBadgeCount(bellcount,countnotification);
                });
        setSupportActionBar(toolbar);
        toolbar.setOverflowIcon(null);
        String openPage=getIntent().getStringExtra("Firsttime");
        bottomnav.setBackground(null);
        String usertype=getIntent().getStringExtra("userType");
        if(openPage.equals("No")){
            if(usertype.equals("Farmer")||usertype.equals("Vendor")){
                openFragement(new HomeFragment(),"Home");
            } else{
                SearchFragment fragment=SearchFragment.newInstance("History");
                openFragement(fragment,"Home");
            }
        } else{
            openFragement(new ProfileFragment(),"Profile");
        }
        if(usertype.equals("Farmer")){
            bottomnav.inflateMenu(R.menu.bottom_navigation_menu_farmer);
        } else if(usertype.equals("Vendor")){
            fab.setVisibility(View.GONE);
            bottomnav.inflateMenu(R.menu.bottom_navigation_menu_vendor);
        } else{
            fab.setVisibility(View.GONE);
            bottomnav.inflateMenu(R.menu.bottom_navigation_menu_researcher);
        }
        ColorStateList iconColorStates = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked},
                        new int[]{android.R.attr.state_checked}
                },
                new int[]{
                        ContextCompat.getColor(this, R.color.black),
                        ContextCompat.getColor(this, R.color.white)
                });

        bottomnav.setItemIconTintList(iconColorStates);
        bottomnav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return selectfragments(item);
            }
        });
        bellicon.setOnClickListener(v->{openFragement(new NotificationFragment(),"Notifications");});
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragement(new HomeFragment(),"Home");
            }
        });
        getUserImage();
        card=findViewById(R.id.roundCardViewProfile);
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Menu menu=toolbar.getMenu();
                for(int i=0;i<menu.size();i++){
                    MenuItem item= menu.getItem(i);
                }
                openOptionsMenu();
            }
        });
    }
    public void init(){
        fab=findViewById(R.id.home);
        bottomnav=findViewById(R.id.bottom_navigation);
        toolbar=findViewById(R.id.MainPageToolbar);
        profileimage=findViewById(R.id.userprofile);
        bellicon=findViewById(R.id.bell_icon);
        bellcount=findViewById(R.id.badge_count);
        pagetitle=findViewById(R.id.PageTitle);
        db=FirebaseFirestore.getInstance();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.user_options,menu);
        return true;
    }
    public boolean selectfragments(MenuItem item){
        int itemid=item.getItemId();
        if(itemid==R.id.Search){
            SearchFragment fragment=SearchFragment.newInstance("Search");
            openFragement(fragment,"Search");
            return true;
        } else if (itemid==R.id.SellProduct) {
            openFragement(new SellFragment(),"Sell");
            return true;
        } else if (itemid==R.id.BuyorRentProduct) {
            openFragement(new CartFragment(),"Cart");
            return true;
        } else if (itemid==R.id.Querys) {
            SearchFragment fragment=SearchFragment.newInstance("Query");
            openFragement(fragment,"Query");
            return true;
        } else if (itemid==R.id.Home) {
            openFragement(new HomeFragment(),"Home");
            return true;
        } else if (itemid==R.id.QuerysHistory) {
            SearchFragment fragment=SearchFragment.newInstance("History");
            openFragement(fragment,"Home");
        }
        return false;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemid=item.getItemId();
        if(itemid==R.id.Profile){
            openFragement(new ProfileFragment(),"Profile");
        } else if(itemid==R.id.Settings){
            openFragement(new SettingFragment(),"Setting");
        } else if (itemid==R.id.Logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent=new Intent(MainPage.this, MainActivity.class);
            startActivity(intent);
        } else {
        }
        return super.onOptionsItemSelected(item);
    }
    private void openFragement(Fragment fragment,String text){
        pagetitle.setText(text);
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout,fragment).commit();
    }
    public void getUserImage(){
        db.collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(value!=null &&value.exists()){
                            Bitmap bitmap= ConvertImage.getBitmap(value.getString("image"));
                            profileimage.setImageBitmap(bitmap);
                        }
                    }
                });
    }
    private void updateBadgeCount(TextView badgeCount, int notificationCount) {
        if (notificationCount > 0) {
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.round_button);
            badgeCount.setVisibility(View.VISIBLE);
            badgeCount.setText(String.valueOf(notificationCount));
            drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(this, R.color.red), PorterDuff.Mode.SRC_IN));
            badgeCount.setBackground(drawable);
        } else {
            badgeCount.setVisibility(View.GONE);
        }
    }
}
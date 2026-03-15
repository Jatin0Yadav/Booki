package com.example.booki;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.booki.Fragments.FragmentCart;
import com.example.booki.Fragments.FragmentHome;
import com.example.booki.Fragments.FragmentOrders;
import com.example.booki.Fragments.FragmentProfile;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Dashboard extends AppCompatActivity {



    @SuppressLint("MissingInflatedId") // when some id doesn't exist, but you still want to use it, when setContentView is called before findViewById.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        BottomNavigationView bnNav = findViewById(R.id.bnview);

        // If we set onClickListener, it will select the bnView, not the icons.
        // To select icons, use NavigationItemSelectedListener.
        bnNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){

            public void loadFrag(Fragment f){
                FragmentManager fr = getSupportFragmentManager();
                FragmentTransaction ft = fr.beginTransaction();
                ft.replace(R.id.frame, f);
                ft.commit();
            }

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if(id == R.id.menu_home){
                    loadFrag(new FragmentHome());
                }

                else if(id == R.id.menu_profile){
                    loadFrag(new FragmentProfile());
                }

                else if(id == R.id.menu_cart){
                    loadFrag(new FragmentCart());
                }
                else{
                    loadFrag(new FragmentOrders());
                }
                return true;                // this return statement should be true, to show the selected icon on bnView.
            }

        });

        bnNav.setSelectedItemId(R.id.menu_home);



    }
}








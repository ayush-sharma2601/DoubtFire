package com.example.doubtfire.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.doubtfire.Fragments.DoubtUploadFragment;
import com.example.doubtfire.Fragments.FeedContainingFragment;
import com.example.doubtfire.Fragments.FeedFragment;
import com.example.doubtfire.Fragments.ProfileFragment;
import com.example.doubtfire.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import me.ibrahimsn.lib.OnItemReselectedListener;
import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    FrameLayout frameLayout;
    TextView heading;
    Toolbar toolbar;
    FirebaseAuth fAuth;
    DatabaseReference userReference;
    SmoothBottomBar smoothBottomBar;
    ImageView menubtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        loadFragment(new FeedContainingFragment());
        heading.setText("Feed");
        menubtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    drawerLayout.openDrawer(Gravity.LEFT);
            }
        });


        smoothBottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelect(int i) {
                switch (i){
                    case 0:
                    {
                        loadFragment(new FeedContainingFragment());
                        heading.setText("Feed");
                        toolbar.setVisibility(View.VISIBLE);
                    }
                    return ;
                    case 2:
                    {
                        loadFragment(new ProfileFragment());
                        heading.setText("Profile");
                        toolbar.setVisibility(View.GONE);
                    }
                    return ;
                    case 1:
                    {
                        loadFragment(new DoubtUploadFragment());
                        heading.setText("New Doubt");
                        toolbar.setVisibility(View.VISIBLE);
                    }
                    return ;
                }
                }

            });
        smoothBottomBar.setOnItemReselectedListener(new OnItemReselectedListener() {
            @Override
            public void onItemReselect(int i) {

            }
        });




    }

    private void init() {
        drawerLayout = findViewById(R.id.drawer);
        menubtn = findViewById(R.id.menu_btn);
        frameLayout = findViewById(R.id.main_frame);
        toolbar = findViewById(R.id.toolbar);
        heading = findViewById(R.id.frag_heading);
        fAuth=FirebaseAuth.getInstance();

        smoothBottomBar = findViewById(R.id.bottom_Bar);
    }
    public void loadFragment(Fragment fragment)
    {
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame,fragment);
//        fragmentTransaction.addToBackStack(null);
//        fragmentTransaction.disallowAddToBackStack();
        fragmentTransaction.commit();
    }

    public void logout(MenuItem item) {
        fAuth.getInstance().signOut();
        drawerLayout.closeDrawer(Gravity.LEFT);
        Toast.makeText(MainActivity.this,"Logout Successful",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void goHome(MenuItem item) {
        drawerLayout.closeDrawer(Gravity.LEFT);
        toolbar.setVisibility(View.VISIBLE);
        loadFragment(new FeedFragment());
    }

    public void goBasicMathPage(MenuItem item) {
        drawerLayout.closeDrawer(Gravity.LEFT);
        Intent goToMathPage = new Intent(MainActivity.this,BasicMathActivity.class);
        startActivity(goToMathPage);
    }
}

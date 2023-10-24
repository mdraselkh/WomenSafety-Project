package com.example.womensafety;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class Homepage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private Button contactbtn,sosbtn,getLocationbtn,detailbtn;

    private  TextView profile_name,profile_mail;

    private View view;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        toolbar=findViewById(R.id.toolbar);
        drawerLayout=findViewById(R.id.drawer_layout);
        navigationView=findViewById(R.id.navigation_view);
        view=navigationView.getHeaderView(0);


        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();

        databaseReference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());


        profile_name =view.findViewById(R.id.usname);
        profile_mail=view.findViewById(R.id.email);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                UserDetails userDetails=snapshot.getValue(UserDetails.class);
                if(userDetails!=null){
                    profile_name.setText(userDetails.getFullname());
                    profile_mail.setText(userDetails.getEmail());
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                Toast.makeText(Homepage.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        contactbtn=findViewById(R.id.contact_btn);
        sosbtn=findViewById(R.id.sos_btn);
        getLocationbtn=findViewById(R.id.loc_btn);
        detailbtn=findViewById(R.id.details_btn);


        getLocationbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(Homepage.this,googleMap.class);
                startActivity(intent);

            }
        });

        contactbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(Homepage.this,addContact.class);
                startActivity(intent);

            }
        });


        detailbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Homepage.this,details.class);
                startActivity(intent);
            }
        });




        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toogle =new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.open,
                R.string.close
        );

        drawerLayout.addDrawerListener(toogle);
        toogle.syncState();

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:

                break;


            case R.id.nav_profile:

                Intent profileIntent=new Intent(Homepage.this,profile.class);
                startActivity(profileIntent);

                break;
            case R.id.nav_logout:

                firebaseAuth.signOut();
                finish();

                Intent logout_intent = new Intent(getApplicationContext(), Login.class);
                logout_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(logout_intent);


                break;
            case R.id.nav_share:

                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String Body="Download this app";
                String Sub="http://play.google.com";
                intent.putExtra(Intent.EXTRA_TEXT,Body);
                intent.putExtra(Intent.EXTRA_TEXT,Sub);
                startActivity(Intent.createChooser(intent,"ShareVia"));

                break;
            case R.id.nav_rate:

                Intent rateIntent=new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com"));
                startActivity(rateIntent);
                break;

            case R.id.nav_help:
                Intent helpIntent=new Intent(Homepage.this,helpGuide.class);
                startActivity(helpIntent);
                break;
            case R.id.nav_about:
                Intent aboutIntent=new Intent(Homepage.this,about.class);
                startActivity(aboutIntent);
                break;

            default:
                break;

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }



}
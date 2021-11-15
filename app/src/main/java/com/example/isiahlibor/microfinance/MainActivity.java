package com.example.isiahlibor.microfinance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawer;
    int id;
    TextView fullname, email;
    Intent intent;
    FirebaseUser user;

    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    private DatabaseReference tblUser, myAddress;

    double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        intent = new Intent(MainActivity.this, LoginActivity.class);
        // go back to login if no current user
        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(intent);
        }
        // loading
        progressDialog = new ProgressDialog(this);
        // get current user
        user = firebaseAuth.getCurrentUser();
        // side menu
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        fullname = (TextView)headerView.findViewById(R.id.fullname);
        email = (TextView)headerView.findViewById(R.id.email);

        GPSTracker gps = new GPSTracker (this);
        latitude = gps.getLatitude();
        longitude= gps.getLongitude();

        // call table users
        myAddress = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());

        myAddress.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // query for update
                dataSnapshot.getRef().child("latitude").setValue(latitude);
                dataSnapshot.getRef().child("longitude").setValue(longitude);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        setHeader();

        // set default selected to dashboard
        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DashboardFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_dashboard);
        }

    }
    // set basic user info
    public void setHeader(){
        // call table users
        tblUser = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());

        tblUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // set data from firebase
                fullname.setText(dataSnapshot.child("firstname").getValue().toString() + " " + dataSnapshot.child("lastname").getValue().toString());
                email.setText(""+user.getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    // navigation tabs
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // display selected
        switch (item.getItemId()){

            case R.id.nav_dashboard:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DashboardFragment()).commit();
                break;

            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                break;

            case R.id.nav_loan:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LoanFragment()).commit();
                break;

            case R.id.nav_lend:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LendFragment()).commit();
                break;

            case R.id.nav_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
                break;

            case R.id.nav_wallet:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new WalletFragment()).commit();
                break;

            case R.id.nav_notify:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NotificationFragment()).commit();
                break;

            case R.id.nav_signout:
                // loading
                progressDialog.setMessage("Signing out...");
                progressDialog.show();

                finish();
                // sign out
                firebaseAuth.signOut();
                // back to login activity
                startActivity(intent);
                break;

        }
        // close the side menu
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    @Override
    public void onBackPressed(){

        if(drawer.isDrawerOpen(GravityCompat.START)){

            drawer.closeDrawer(GravityCompat.START);

        }else{

            super.onBackPressed();

        }

    }

}

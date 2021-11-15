package com.example.isiahlibor.microfinance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

//    Bundle bundle;
//    int id;
    Fragment selectedFragment = null;
    Intent intent;
    FirebaseUser user;
    private DatabaseReference tblStore;
    private FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // load fragment_profile.xml layout
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        intent = new Intent(getContext(), LoginActivity.class);
        // go to login if no current user
        if(firebaseAuth.getCurrentUser() == null){
            startActivity(intent);
        }
        // set bottom navigation
        BottomNavigationView bottomNav = view.findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // set default fragment on load
        getFragmentManager().beginTransaction().replace(R.id.fragment_bottomNav, new AccountFragment()).commit();

        return view;

    }

    // bottom navigation
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            // get selected navigation
            switch(item.getItemId()){

                case R.id.nav_account:
                    selectedFragment = new AccountFragment(); // launch AccountFragment
                    getFragmentManager().beginTransaction().replace(R.id.fragment_bottomNav, selectedFragment).commit();
                    break;

                case R.id.nav_business:
                    store();
                    break;

            }

            return true;

        }

    };

    // launch CreateStoreFragment if no detected Sari Sari Store, else display BusinessFragment
    public void store(){

            firebaseAuth = FirebaseAuth.getInstance();
            // get current user
            user = firebaseAuth.getCurrentUser();
            // call table stores
            tblStore = FirebaseDatabase.getInstance().getReference().child("Stores").child(user.getUid());

            tblStore.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.child("title").getValue().toString().equals("none")){
                        selectedFragment = new CreateStoreFragment(); // display CreateStoreFragment
                        getFragmentManager().beginTransaction().replace(R.id.fragment_bottomNav, selectedFragment).commit();
                    }else{
                        selectedFragment = new BusinessFragment(); // display BusinessFragment
                        getFragmentManager().beginTransaction().replace(R.id.fragment_bottomNav, selectedFragment).commit();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

    }


}

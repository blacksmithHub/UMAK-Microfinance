package com.example.isiahlibor.microfinance;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CreateStoreFragment extends Fragment {

    EditText title, address;
    Button create;
    FirebaseUser user;

    private FirebaseAuth firebaseAuth;

    private DatabaseReference tblStore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // load fragment_create_store.xml layout
        View view = inflater.inflate(R.layout.fragment_create_store, container, false);

        title = (EditText)view.findViewById(R.id.title);
        address = (EditText)view.findViewById(R.id.address);
        create = (Button)view.findViewById(R.id.create);

        firebaseAuth = FirebaseAuth.getInstance();
        // get current user
        user = firebaseAuth.getCurrentUser();

        create();

        return view;
    }

    // create button
    public void create(){

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // call table stores
                tblStore = FirebaseDatabase.getInstance().getReference().child("Stores").child(user.getUid());

                tblStore.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // query for update
                            dataSnapshot.getRef().child("title").setValue(title.getText().toString().trim());
                            dataSnapshot.getRef().child("address").setValue(address.getText().toString().trim());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                // after creating store, go to BusinessFragment
                getFragmentManager().beginTransaction().replace(R.id.fragment_bottomNav, new BusinessFragment()).commit();

            }
        });

    }
}

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BusinessFragment extends Fragment {

    EditText title, address;
    Button update, cancel, submit;
    FirebaseUser user;

    private DatabaseReference tblStore;

    private FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        // load fragment_business.xml layout
        View view = inflater.inflate(R.layout.fragment_business, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        title = (EditText)view.findViewById(R.id.title);
        address = (EditText)view.findViewById(R.id.address);
        update = (Button)view.findViewById(R.id.update);
        submit = (Button)view.findViewById(R.id.submit);
        cancel = (Button)view.findViewById(R.id.cancel);

        // get current user
        user = firebaseAuth.getCurrentUser();

        update();
        submit();
        cancel();

        populate();

        return view;
    }

    // update button
    public void update(){

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cancel.setVisibility(View.VISIBLE);
                update.setVisibility(View.GONE);
                submit.setVisibility(View.VISIBLE);

                title.setEnabled(true);
                address.setEnabled(true);

            }
        });

    }

    // cancel button
    public void cancel(){

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cancel.setVisibility(View.GONE);
                update.setVisibility(View.VISIBLE);
                submit.setVisibility(View.GONE);

                title.setEnabled(false);
                address.setEnabled(false);

            }
        });

    }

    // submit button
    public void submit(){

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // call table stores
                tblStore = FirebaseDatabase.getInstance().getReference().child("Stores").child(user.getUid());

                tblStore.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        // query for update
                        dataSnapshot.getRef().child("title").setValue(title.getText().toString());
                        dataSnapshot.getRef().child("address").setValue(address.getText().toString());

                        cancel.setVisibility(View.GONE);
                        update.setVisibility(View.VISIBLE);
                        submit.setVisibility(View.GONE);

                        title.setEnabled(false);
                        address.setEnabled(false);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

    }

    // set default
    public void populate(){
        // call table stores
        tblStore = FirebaseDatabase.getInstance().getReference().child("Stores").child(user.getUid());

        tblStore.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // set values
                title.setText(""+dataSnapshot.child("title").getValue().toString());
                address.setText(""+dataSnapshot.child("address").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}

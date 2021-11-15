package com.example.isiahlibor.microfinance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import com.sqlitelib.DataBaseHelper;
import com.sqlitelib.SQLite;

public class AccountFragment extends Fragment {

    EditText firstname, lastname, email, password;
    Button update, submit, cancel;

    FirebaseUser user;
    Intent intent;

    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    private DatabaseReference tblUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // load fragment_account.xml layout
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        intent = new Intent(getContext(), LoginActivity.class);
        // go back to login if no current user
        if(firebaseAuth.getCurrentUser() == null){
            startActivity(intent);
        }
        // loading
        progressDialog = new ProgressDialog(getContext());
        // get current user
        user = firebaseAuth.getCurrentUser();

        // input fields
        firstname = (EditText) view.findViewById(R.id.firstname);
        lastname = (EditText) view.findViewById(R.id.lastname);
        email = (EditText) view.findViewById(R.id.email);
        password = (EditText) view.findViewById(R.id.password);
        update = (Button) view.findViewById(R.id.update);
        submit = (Button) view.findViewById(R.id.submit);
        cancel = (Button) view.findViewById(R.id.cancel);

        populate();
        submit();
        cancel();
        update();

        return view;
    }

    // populate fields
    public void populate(){
        // call table users
        tblUser = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());

        tblUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // set values
                firstname.setText(""+dataSnapshot.child("firstname").getValue().toString());
                lastname.setText(""+dataSnapshot.child("lastname").getValue().toString());
                email.setText(""+dataSnapshot.child("email").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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

                firstname.setEnabled(false);
                lastname.setEnabled(false);
                email.setEnabled(false);

            }
        });
    }

    // submit button
    public void submit(){
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // call table users
                tblUser = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());

                tblUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // query for update
                        dataSnapshot.getRef().child("firstname").setValue(firstname.getText().toString().trim());
                        dataSnapshot.getRef().child("lastname").setValue(lastname.getText().toString().trim());
                        dataSnapshot.getRef().child("email").setValue(email.getText().toString().trim());

                        cancel.setVisibility(View.GONE);
                        update.setVisibility(View.VISIBLE);
                        submit.setVisibility(View.GONE);

                        firstname.setEnabled(false);
                        lastname.setEnabled(false);
                        email.setEnabled(false);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    // update button
    public void update(){

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cancel.setVisibility(View.VISIBLE);
                update.setVisibility(View.GONE);
                submit.setVisibility(View.VISIBLE);

                firstname.setEnabled(true);
                lastname.setEnabled(true);
                email.setEnabled(true);

            }
        });

    }
}

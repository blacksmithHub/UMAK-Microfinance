package com.example.isiahlibor.microfinance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SettingsFragment extends Fragment {

    Button deactivate;
    FirebaseUser user;
    Intent intent;

    private DatabaseReference tblUser;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // load fragment_settings.xml layout
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        intent = new Intent(getContext(), LoginActivity.class);
        // go to login if no current user
        if(firebaseAuth.getCurrentUser() == null){
            startActivity(intent);
        }
        // loading
        progressDialog = new ProgressDialog(getContext());

        user = FirebaseAuth.getInstance().getCurrentUser();

        deactivate = (Button)view.findViewById(R.id.deactivate);

        deactivate();

        return view;
    }

    // hard delete user account
    public void deactivate(){

        deactivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // loading
                progressDialog.setMessage("Signing out...");
                progressDialog.show();
                // call table user
                tblUser = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                // delete user authentication
                AuthCredential credential = EmailAuthProvider.getCredential("angelo@gmail.com", "qweqwe");
                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // delete query
                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                // delete user in table users
                                tblUser.removeValue();
                                // redirect login
                                startActivity(intent);
                            }
                        });
                    }
                });

            }
        });

    }
}

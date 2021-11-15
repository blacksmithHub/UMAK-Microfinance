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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class WalletFragment extends Fragment {

    EditText money;
    Button deposit;
    private DatabaseReference database;
    private FirebaseAuth firebaseAuth;
    FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // load fragment_settings.xml layout
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        money = (EditText)view.findViewById(R.id.money);
        deposit = (Button)view.findViewById(R.id.deposit);

        deposit();

        return view;
    }

    public void deposit(){

        deposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // create table goals
                database = FirebaseDatabase.getInstance().getReference().child("Wallets").child(user.getUid());
                // create columns with values, set to 'none' for default
                HashMap<String, String> goalMap = new HashMap<>();
                goalMap.put("user_id", user.getUid());
                goalMap.put("amount", money.getText().toString());

                database.setValue(goalMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                });

            }
        });

    }

}

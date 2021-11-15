package com.example.isiahlibor.microfinance;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NotificationFragment extends Fragment {

    ListView notify;
    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    private DatabaseReference tblLoans, tblStores, tblUsers;
    FirebaseUser user;
    private FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // load fragment_settings.xml layout
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        // get current user
        user = firebaseAuth.getCurrentUser();

        notify = (ListView)view.findViewById(R.id.notify);
        list = new ArrayList<>();

        tblLoans = FirebaseDatabase.getInstance().getReference().child("Loans");

        tblLoans.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.child("borrower_id").getValue().toString().equals(user.getUid()) && snapshot.child("status").getValue().toString().equals("pending") ){
                        tblStores = FirebaseDatabase.getInstance().getReference().child("Stores").child(snapshot.child("borrower_id").getValue().toString());
                        tblStores.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.child("title").getValue().toString().equals("none")){
                                    tblUsers = FirebaseDatabase.getInstance().getReference().child("Stores").child(dataSnapshot.child("borrower_id").getValue().toString());
                                    tblUsers.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            list.add(dataSnapshot.child("firstname").getValue().toString() + " " + dataSnapshot.child("lastname").getValue().toString());
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }else{
                                    list.add(dataSnapshot.child("title").getValue().toString());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                }
                if(adapter == null){
                    adapter = new ArrayAdapter<String>(getContext(), R.layout.notify, R.id.title, list);
                    notify.setAdapter(adapter);
                }else{
                    adapter.clear();
                    adapter.addAll(list);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return view;
    }


}

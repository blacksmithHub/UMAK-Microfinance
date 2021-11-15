package com.example.isiahlibor.microfinance;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
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
import java.util.List;

public class LoanFragment extends Fragment {

    private DatabaseReference tblGoals, tblStores, tblLoans, tblUsers, tblWallet;
    FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    TextView goal;
    ListView loan;
    private ArrayList<String> list, lender_id;
    ArrayAdapter<String> adapter;
    String fname, lname, lender, amount;
    Double loanAmount, money, total;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // load fragment_settings.xml layout
        View view = inflater.inflate(R.layout.fragment_loan, container, false);

        goal = (TextView) view.findViewById(R.id.goalAmount);
        loan = (ListView) view.findViewById(R.id.loan);
        list = new ArrayList<>();
        lender_id = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();
        // get current user
        user = firebaseAuth.getCurrentUser();

        tblStores = FirebaseDatabase.getInstance().getReference().child("Stores").child(user.getUid());

        tblStores.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.child("title").getValue().toString().equals("none")) {

                    tblGoals = FirebaseDatabase.getInstance().getReference().child("Goals").child(user.getUid());

                    tblGoals.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.child("status").getValue().toString().equals("none")) {

                                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new GoalFragment()).commit();

                            } else if (dataSnapshot.child("status").getValue().toString().equals("completed")) {


                            } else {
                                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new ScheduleOfPaymentFragment()).commit();
//                                goal.setText("Goal: "+dataSnapshot.child("amount").getValue().toString());
//                                tblLoans = FirebaseDatabase.getInstance().getReference().child("Transaction");
//                                tblLoans.addValueEventListener(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                                            if (snapshot.child("borrower_id").getValue().toString().equals(user.getUid()) && snapshot.child("paid").getValue().toString().equals("no")) {
//
//                                                final String lend = snapshot.child("amount").getValue().toString();
//
//                                                tblUsers = FirebaseDatabase.getInstance().getReference("Users").child(snapshot.child("lender_id").getValue().toString());
//                                                tblUsers.addValueEventListener(new ValueEventListener() {
//                                                    @Override
//                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                                                        fname = dataSnapshot.child("firstname").getValue().toString();
//                                                        lname = dataSnapshot.child("lastname").getValue().toString();
//                                                        lender = dataSnapshot.child("user_id").getValue().toString();
//
//                                                        list.add( fname + " " + lname + " - " + lend);
//                                                        lender_id.add(lender);
//
//                                                        adapter = new ArrayAdapter<String>(getContext(), R.layout.loan, R.id.title, list);
//                                                        loan.setAdapter(adapter);
//
//
//
//                                                    }
//
//                                                    @Override
//                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                                    }
//                                                });
//
//                                            }
//
//                                        }
//
//
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                    }
//                                });
//
//                                loan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                                    @Override
//                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                                        Toast.makeText(getContext(),""+lender_id.get(position),Toast.LENGTH_LONG).show();
//                                        final int ids = position;
//                                        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
//                                        alertDialog.setTitle("Loan");
//                                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "PAY",
//                                                new DialogInterface.OnClickListener() {
//                                                    public void onClick(DialogInterface dialog, int which) {
//
//                                                        tblLoans = FirebaseDatabase.getInstance().getReference("Transaction").child(lender_id.get(ids));
//
//                                                        tblLoans.addValueEventListener(new ValueEventListener() {
//                                                            @Override
//                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                                                                    if(dataSnapshot.child("borrower_id").getValue().toString().equals(user.getUid()) &&
//                                                                            dataSnapshot.child("lender_id").getValue().toString().equals(lender_id.get(ids))){
//
//                                                                        amount = dataSnapshot.child("amount").getValue().toString();
//
////                                                                        dataSnapshot.getRef().child("amount").setValue("0");
////                                                                        dataSnapshot.getRef().child("paid").setValue("yes");
//
////                                                                        tblWallet = FirebaseDatabase.getInstance().getReference("Wallets").child(dataSnapshot.child("lender_id").getValue().toString());
////
////                                                                        tblWallet.addValueEventListener(new ValueEventListener() {
////                                                                            @Override
////                                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////
////                                                                                loanAmount = Double.parseDouble(amount);
////
////                                                                                money = Double.parseDouble(dataSnapshot.child("amount").getValue().toString());
////
////                                                                                total = money + loanAmount;
////
////                                                                                dataSnapshot.getRef().child("amount").setValue(String.valueOf(total));
////
////                                                                            }
////
////                                                                            @Override
////                                                                            public void onCancelled(@NonNull DatabaseError databaseError) {
////
////                                                                            }
////                                                                        });
//
////                                                                        tblWallet = FirebaseDatabase.getInstance().getReference("Wallets").child(snapshot.child("borrower_id").getValue().toString());
////
////                                                                        tblWallet.addValueEventListener(new ValueEventListener() {
////                                                                            @Override
////                                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////
////                                                                                loanAmount = Double.parseDouble(amount);
////
////                                                                                money = Double.parseDouble(dataSnapshot.child("amount").getValue().toString());
////
////                                                                                dataSnapshot.getRef().child("amount").setValue(String.valueOf(money - loanAmount));
////
////                                                                            }
////
////                                                                            @Override
////                                                                            public void onCancelled(@NonNull DatabaseError databaseError) {
////
////                                                                            }
////                                                                        });
//
//                                                                    }
//
//
//                                                            }
//
//                                                            @Override
//                                                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                                            }
//                                                        });
//
//
//
//                                                        dialog.dismiss();
//                                                    }
//                                                });
//                                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "CLOSE",
//                                                new DialogInterface.OnClickListener() {
//                                                    public void onClick(DialogInterface dialog, int which) {
//                                                        dialog.dismiss();
//                                                    }
//                                                });
//                                        alertDialog.show();
//                                    }
//                                });

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }


}

package com.example.isiahlibor.microfinance;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Random;

public class Lend_detailsFragment extends Fragment {

    private DatabaseReference tblStore, tblUser, tblLoan, tblCheck, tblGoal, tblWallet;
    String firstname, lastname, email, title, address, rate, borrower_id;
    Double getAmount;
    TextView fullname, mail, store, location, interest;
    Button loan;
    EditText amount;
    private ProgressDialog progressDialog;
    FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    Double minus, minus1, minus2, goalAmount, total, wallet;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // load fragment_settings.xml layout
        View view = inflater.inflate(R.layout.fragment_lend_details, container, false);

        fullname = (TextView)view.findViewById(R.id.fullname);
        mail = (TextView)view.findViewById(R.id.mail);
        store = (TextView)view.findViewById(R.id.store);
        location = (TextView)view.findViewById(R.id.location);
        interest = (TextView)view.findViewById(R.id.rate);
        loan = (Button)view.findViewById(R.id.lend);
        amount = (EditText)view.findViewById(R.id.amount);

        loan();

        borrower_id = getArguments().getString("id");
        firebaseAuth = FirebaseAuth.getInstance();
        // get current user
        user = firebaseAuth.getCurrentUser();

        tblStore = FirebaseDatabase.getInstance().getReference().child("Stores").child(borrower_id);

        tblStore.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(!dataSnapshot.child("title").getValue().toString().equals("none")){

                        title = dataSnapshot.child("title").getValue().toString();
                        address = dataSnapshot.child("address").getValue().toString();

                        store.setText(title);
                        location.setText(address);

                        tblUser = FirebaseDatabase.getInstance().getReference().child("Users").child(borrower_id);

                        tblUser.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                firstname = dataSnapshot.child("firstname").getValue().toString();
                                lastname = dataSnapshot.child("lastname").getValue().toString();
                                email = dataSnapshot.child("email").getValue().toString();

                                fullname.setText(firstname + " " + lastname);
                                mail.setText(email);

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

    public void loan(){

        loan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    tblCheck = FirebaseDatabase.getInstance().getReference().child("Transaction").child(user.getUid());

                    tblCheck.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if(dataSnapshot.hasChildren() == true){

                                        if(dataSnapshot.child("lender_id").getValue().toString().equals(user.getUid()) &&
                                                dataSnapshot.child("borrower_id").getValue().toString().equals(borrower_id) && dataSnapshot.child("paid").getValue().toString().equals("yes")){
                                            getAmount = Double.parseDouble(amount.getText().toString());

                                            // create table transaction
                                            tblLoan = FirebaseDatabase.getInstance().getReference().child("Transaction").child(user.getUid());
                                            // create columns with values, set to 'none' for default
                                            HashMap<String, String> storeMap = new HashMap<>();
                                            storeMap.put("transaction_id", user.getUid());
                                            storeMap.put("lender_id", user.getUid());
                                            storeMap.put("borrower_id", borrower_id);
                                            storeMap.put("amount", amount.getText().toString());
                                            storeMap.put("paid", "no");

                                            tblLoan.setValue(storeMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                }
                                            });
                                            transaction();
                                        }else if(dataSnapshot.child("lender_id").getValue().toString().equals(user.getUid()) &&
                                                dataSnapshot.child("borrower_id").getValue().toString().equals(borrower_id) && dataSnapshot.child("paid").getValue().toString().equals("no")){
                                            Toast.makeText(getContext(),"This borrower is not yet paid",Toast.LENGTH_LONG).show();
                                        }
                                        else{

                                            getAmount = Double.parseDouble(amount.getText().toString());

                                            // create table transaction
                                            tblLoan = FirebaseDatabase.getInstance().getReference().child("Transaction").child(user.getUid());
                                            // create columns with values, set to 'none' for default
                                            HashMap<String, String> storeMap = new HashMap<>();
                                            storeMap.put("transaction_id", user.getUid());
                                            storeMap.put("lender_id", user.getUid());
                                            storeMap.put("borrower_id", borrower_id);
                                            storeMap.put("amount", amount.getText().toString());
                                            storeMap.put("paid", "no");

                                            tblLoan.setValue(storeMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                }
                                            });

                                            transaction();
                                        }


                                }else{
                                    // create table transaction
                                    tblLoan = FirebaseDatabase.getInstance().getReference().child("Transaction").child(user.getUid());
                                    // create columns with values, set to 'none' for default
                                    HashMap<String, String> storeMap = new HashMap<>();
                                    storeMap.put("transaction_id", user.getUid());
                                    storeMap.put("lender_id", user.getUid());
                                    storeMap.put("borrower_id", borrower_id);
                                    storeMap.put("amount", amount.getText().toString());
                                    storeMap.put("paid", "no");

                                    tblLoan.setValue(storeMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            transaction();
                                            getFragmentManager().beginTransaction().replace(R.id.fragment_container, new LendFragment()).commit();
                                        }
                                    });
                                }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


            }
        });

    }

    public void transaction(){

        tblLoan = FirebaseDatabase.getInstance().getReference().child("Transaction").child(user.getUid());

        tblLoan.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                minus = Double.parseDouble(dataSnapshot.child("amount").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        tblGoal = FirebaseDatabase.getInstance().getReference().child("Goals").child(borrower_id);

        tblGoal.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    goalAmount = Double.parseDouble(dataSnapshot.child("amount").getValue().toString());
                    total = goalAmount - minus;

                    dataSnapshot.getRef().child("amount").setValue(String.valueOf(total));

                    minus = 0.0;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        tblLoan = FirebaseDatabase.getInstance().getReference().child("Transaction").child(user.getUid());

        tblLoan.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                minus1 = Double.parseDouble(dataSnapshot.child("amount").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        tblWallet = FirebaseDatabase.getInstance().getReference().child("Wallets").child(borrower_id);

        tblWallet.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                wallet = Double.parseDouble(dataSnapshot.child("amount").getValue().toString());
                total = wallet + minus1;

                dataSnapshot.getRef().child("amount").setValue(String.valueOf(total));

                minus1 = 0.0;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        tblLoan = FirebaseDatabase.getInstance().getReference().child("Transaction").child(user.getUid());

        tblLoan.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                minus2 = Double.parseDouble(dataSnapshot.child("amount").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        tblWallet = FirebaseDatabase.getInstance().getReference().child("Wallets").child(user.getUid());

        tblWallet.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                wallet = Double.parseDouble(dataSnapshot.child("amount").getValue().toString());
                total = wallet - minus2;

                dataSnapshot.getRef().child("amount").setValue(String.valueOf(total));

                minus2 = 0.0;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }


}

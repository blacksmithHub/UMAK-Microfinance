package com.example.isiahlibor.microfinance;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GoalFragment extends Fragment {

//    EditText amount, rate;
//    Button create;

    RadioGroup group;
    RadioButton radio;
    Button next;
    String amount;

//    private DatabaseReference tblGoal;
//    private FirebaseAuth firebaseAuth;
//    FirebaseUser user;

    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // load fragment_settings.xml layout
        view = inflater.inflate(R.layout.fragment_goal, container, false);

//        firebaseAuth = FirebaseAuth.getInstance();
//        // get current user
//        user = firebaseAuth.getCurrentUser();

//        amount = (EditText)view.findViewById(R.id.amount);
//        rate = (EditText)view.findViewById(R.id.rate);
//        create = (Button)view.findViewById(R.id.create);
//
//        create();

        group = (RadioGroup)view.findViewById(R.id.group);
        next = (Button)view.findViewById(R.id.next);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = group.getCheckedRadioButtonId();
                radio = (RadioButton)view.findViewById(selectedId);
                if(radio.getText().equals("10,000")){
                    amount = "10000.00";
                }else if(radio.getText().equals("20,000")){
                    amount = "20000.00";
                }else if(radio.getText().equals("30,000")){
                    amount = "30000.00";
                }else if(radio.getText().equals("40,000")){
                    amount = "40000.00";
                }else if(radio.getText().equals("50,000")){
                    amount = "50000.00";
                }
                Fragment frag = new TermsOfPaymentFragment();
                Bundle args = new Bundle();
                args.putString("loanAmount", amount);
                frag.setArguments(args);
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, frag).commit();
            }
        });

        return view;
    }

//    public void create(){
//
//        create.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                try {
//                    tblGoal = FirebaseDatabase.getInstance().getReference().child("Goals").child(user.getUid());
//
//                    tblGoal.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                            dataSnapshot.getRef().child("amount").setValue(amount.getText().toString());
//                            dataSnapshot.getRef().child("rate").setValue(rate.getText().toString());
//                            dataSnapshot.getRef().child("status").setValue("ongoing");
//
//                            getFragmentManager().beginTransaction().replace(R.id.fragment_container, new LoanFragment()).commit();
//
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });
//                } catch (Exception e) {
//                    Toast.makeText(getContext(),""+e,Toast.LENGTH_LONG).show();
//                }
//
//            }
//        });
//
//    }

}

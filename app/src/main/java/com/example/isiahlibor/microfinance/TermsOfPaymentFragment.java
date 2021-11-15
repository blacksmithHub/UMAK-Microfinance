package com.example.isiahlibor.microfinance;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class TermsOfPaymentFragment extends Fragment {

    View view;
    RadioGroup group;
    RadioButton radio;
    String loanAmount, rate;
    Calendar cal;
    Button confirm, back;
    double totalLoan, months;
    SimpleDateFormat sdf, sdf1, df;
    private DatabaseReference tblGoal, tblSchedule;
    private FirebaseAuth firebaseAuth;
    FirebaseUser user;
    String date;
    String formattedDate;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_termsofpayment, container, false);
        cal = Calendar.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        // get current user
        user = firebaseAuth.getCurrentUser();

        group = (RadioGroup)view.findViewById(R.id.group);
        confirm = (Button)view.findViewById(R.id.confirm);
        back = (Button)view.findViewById(R.id.back);

        loanAmount = getArguments().getString("loanAmount");

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = group.getCheckedRadioButtonId();
                radio = (RadioButton)view.findViewById(selectedId);
                if(radio.getText().equals("6 months ( 10% )")){
                    rate = "0.10";
                    months = 6.0;
                }else{
                    rate = "0.15";
                    months = 12.0;
                }
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(getContext());
                }
                builder.setTitle("Confirmation")
                        .setMessage("Are you sure you want to start your loan?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue
                                totalLoan = (Double.parseDouble(loanAmount) * Double.parseDouble(rate)) + Double.parseDouble(loanAmount) / months;

                                try {
                                    tblGoal = FirebaseDatabase.getInstance().getReference().child("Goals").child(user.getUid());

                                    tblGoal.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            dataSnapshot.getRef().child("amount").setValue(String.format("%.2f", totalLoan));
                                            dataSnapshot.getRef().child("months").setValue(String.valueOf(months));
                                            dataSnapshot.getRef().child("status").setValue("ongoing");
                                            Date c = Calendar.getInstance().getTime();
                                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                                            formattedDate = df.format(c);
                                            dataSnapshot.getRef().child("created_at").setValue(formattedDate);

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, new ScheduleOfPaymentFragment()).commit();

                                } catch (Exception e) {
                                    Toast.makeText(getContext(),""+e,Toast.LENGTH_LONG).show();
                                }

                                tblSchedule = FirebaseDatabase.getInstance().getReference().child("ScheduleOfPayment").child(user.getUid());
                                HashMap<String, String> userMap = new HashMap<>();
                                userMap.put("borrower_id", user.getUid());
                                userMap.put("lender_id", "none");

                                String[] separated = String.valueOf(months).split("\\.");
                                if(Integer.parseInt(separated[0]) == 6){
                                    date = formattedDate;
                                    for(int a = 0; a < Integer.parseInt(separated[0]); a++){
                                        sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        cal.add(Calendar.DATE, 30);
                                        sdf1 = new SimpleDateFormat("MM-dd-yyyy");
                                        df = new SimpleDateFormat("yyyy-MM-dd");
                                        date = df.format(cal.getTime());
                                        userMap.put(sdf1.format(cal.getTime()), "none");
                                    }
                                }else{
                                    date = formattedDate;
                                    for(int a = 0; a < Integer.parseInt(separated[0]); a++){
                                        sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        cal.add(Calendar.DATE, 365);
                                        sdf1 = new SimpleDateFormat("MM-dd-yyyy");
                                        df = new SimpleDateFormat("yyyy-MM-dd");
                                        date = df.format(cal.getTime());
                                        userMap.put(sdf1.format(cal.getTime()), "none");
                                    }
                                }

                                tblSchedule.setValue(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // successfully created table user
                                    }
                                });

                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new GoalFragment()).commit();
            }
        });

        return view;
    }

}

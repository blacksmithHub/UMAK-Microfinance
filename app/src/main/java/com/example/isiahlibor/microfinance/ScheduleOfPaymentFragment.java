package com.example.isiahlibor.microfinance;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
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
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ScheduleOfPaymentFragment extends Fragment {

    View view;

    private DatabaseReference tblGoal;
    private FirebaseAuth firebaseAuth;
    FirebaseUser user;
    Calendar c;
    String dates;
    SimpleDateFormat sdf, sdf1, df;
    private ListView lv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_scheduleofpayment, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        // get current user
        user = firebaseAuth.getCurrentUser();

        c = Calendar.getInstance();

        lv = (ListView)view.findViewById(R.id.listview);
        final List<String> arrayList = new ArrayList<String>();

        tblGoal = FirebaseDatabase.getInstance().getReference().child("Goals").child(user.getUid());
        tblGoal.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String months = dataSnapshot.child("months").getValue().toString();
                String[] separated = months.split("\\.");
                if(separated[0].equals("6")){
                    dates = dataSnapshot.child("created_at").getValue().toString();
                    for(int a = 0; a < Integer.parseInt(separated[0]); a++){
                        sdf = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            c.setTime(sdf.parse(dates));
                        } catch (ParseException e) {
                            Toast.makeText(getContext(),""+e,Toast.LENGTH_LONG).show();
                        }
                        c.add(Calendar.DATE, 30);
                        sdf1 = new SimpleDateFormat("MM-dd-yyyy");
                        df = new SimpleDateFormat("yyyy-MM-dd");
                        dates = df.format(c.getTime());
                        arrayList.add(sdf1.format(c.getTime()) + " - " + dataSnapshot.child("amount").getValue().toString());
                    }
                }else{
                    dates = dataSnapshot.child("created_at").getValue().toString();
                    for(int a = 0; a < Integer.parseInt(separated[0]); a++){
                        sdf = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            c.setTime(sdf.parse(dates));
                        } catch (ParseException e) {
                            Toast.makeText(getContext(),""+e,Toast.LENGTH_LONG).show();
                        }
                        c.add(Calendar.DATE, 365);
                        sdf1 = new SimpleDateFormat("MM-dd-yyyy");
                        df = new SimpleDateFormat("yyyy-MM-dd");
                        dates = df.format(c.getTime());
                        arrayList.add(sdf1.format(c.getTime()) + " - " + dataSnapshot.child("amount").getValue().toString());
                    }
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, arrayList );
                lv.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {
//                AlertDialog.Builder builder;
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
//                } else {
//                    builder = new AlertDialog.Builder(getContext());
//                }
//                builder.setTitle("Paid")
//                        .setMessage("Mark it as paid?")
//                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                // continue
//                                view.setBackgroundColor(Color.parseColor("#4BB7E2"));
//                            }
//                        })
//                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                // do nothing
//                            }
//                        })
//                        .show();
//
//                return false;
//            }
//        });

        return view;
    }

}

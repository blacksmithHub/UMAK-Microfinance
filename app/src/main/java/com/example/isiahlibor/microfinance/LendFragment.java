package com.example.isiahlibor.microfinance;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class LendFragment extends Fragment implements OnMapReadyCallback {

    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static float DEFAULT_ZOOM = 15f;

    private boolean mLocationPermissionGranted = false;

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    FirebaseUser user;
    private DatabaseReference tblGoals, myAddress, tblStores, tblTransaction, tblUser;
    private FirebaseAuth firebaseAuth;
    List<Address> addresses;
    ListView lend;
    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    Geocoder geo;
    Location currentLocation;
    private static Marker loan, mine;
    String id, goal, amount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // load fragment_lend.xml layout
        View view = inflater.inflate(R.layout.fragment_lend, container, false);

        lend = (ListView)view.findViewById(R.id.lend);
        list = new ArrayList<>();

        geo = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
        addresses = null;

        firebaseAuth = FirebaseAuth.getInstance();
        // get current user
        user = firebaseAuth.getCurrentUser();
        // call table stores
        tblGoals = FirebaseDatabase.getInstance().getReference().child("Stores").child(user.getUid());

        tblGoals.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

//                if (dataSnapshot.child("title").getValue().toString().equals("none")) {
//                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, new CreateStoreFragment()).commit();
//                } else {
//                    getLocationPermission();
//                }

                lend();
                mLocationPermissionGranted = true;

                initMap();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    private void lend(){

        tblTransaction = FirebaseDatabase.getInstance().getReference().child("Transaction");

        tblTransaction.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    if(snapshot.child("lender_id").getValue().toString().equals(user.getUid())){

                        amount = snapshot.child("amount").getValue().toString();

                        tblUser = FirebaseDatabase.getInstance().getReference().child("Users").child(snapshot.child("borrower_id").getValue().toString());

                        tblUser.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                list.add(dataSnapshot.child("firstname").getValue().toString() + " " + dataSnapshot.child("lastname").getValue().toString() + " - " + amount);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                }

                if(adapter == null){
                    adapter = new ArrayAdapter<String>(getContext(), R.layout.lend, R.id.title, list);
                    lend.setAdapter(adapter);
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

        lend.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                return false;
            }
        });

    }

    private void getDeviceLocation() {

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        try {
            if (mLocationPermissionGranted) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            try {
                            currentLocation = (Location) task.getResult();
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);
                            Toast.makeText(getContext(),""+currentLocation.getLatitude(),Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                Toast.makeText(getContext(),""+e,Toast.LENGTH_LONG).show();
                            }
                            try {
                                addresses = geo.getFromLocation(currentLocation.getLatitude(), currentLocation.getLatitude(), 1);
                                if (addresses.isEmpty()) {
                                    Toast.makeText(getContext(),"Waiting for location...",Toast.LENGTH_LONG).show();
                                }
                                else {
                                    if (addresses.size() > 0) {
                                        mine = mMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                                                .anchor(0.5f, 0.5f)
                                                .title("Me")
                                                .snippet(addresses.get(0).getLocality()));

                                        CircleOptions circleOptions = new CircleOptions();

                                        // Specifying the center of the circle
                                        circleOptions.center(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));

                                        // Radius of the circle
                                        circleOptions.radius(1500);

                                        // Border width of the circle
                                        circleOptions.strokeWidth(1);

                                        // Adding the circle to the GoogleMap
                                        mMap.addCircle(circleOptions);

                                        float[] distance = new float[2];

                                        Location.distanceBetween(mine.getPosition().latitude, mine.getPosition().longitude,
                                                circleOptions.getCenter().latitude, circleOptions.getCenter().longitude, distance);

                                        if(distance[0] > circleOptions.getRadius()){
                                            Toast.makeText(getContext(), "Outside", Toast.LENGTH_LONG).show();
                                        } else {
                                            tblGoals = FirebaseDatabase.getInstance().getReference().child("Goals");

                                            tblGoals.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                                        if(!snapshot.child("user_id").getValue().toString().equals(user.getUid()) && !snapshot.child("status").getValue().toString().equals("none")){

                                                            goal = String.valueOf(Double.parseDouble(snapshot.child("amount").getValue().toString()) * Double.parseDouble(snapshot.child("months").getValue().toString()));

                                                            tblStores = FirebaseDatabase.getInstance().getReference().child("Stores").child(snapshot.child("user_id").getValue().toString());
                                                            tblStores.addValueEventListener(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    try {
                                                                        addresses = geo.getFromLocationName(dataSnapshot.child("address").getValue().toString(), 1);
                                                                        if (addresses.isEmpty()) {
                                                                            Toast.makeText(getContext(),"Waiting for location...",Toast.LENGTH_LONG).show();
                                                                        }
                                                                        else {
                                                                            if (addresses.size() > 0) {

                                                                                loan = mMap.addMarker(new MarkerOptions()
                                                                                        .position(new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude()))
                                                                                        .anchor(0.5f, 0.5f)
                                                                                        .title(dataSnapshot.child("title").getValue().toString())
                                                                                        .snippet(goal));

                                                                                id = dataSnapshot.child("user_id").getValue().toString();

                                                                                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                                                                    @Override
                                                                                    public void onInfoWindowClick(Marker marker) {
//                                                                                        AlertDialog.Builder builder;
//                                                                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                                                                                            builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
//                                                                                        } else {
//                                                                                            builder = new AlertDialog.Builder(getContext());
//                                                                                        }
//                                                                                        builder.setTitle("Confirmation")
//                                                                                                .setMessage("Are you sure you want to start your loan?")
//                                                                                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                                                                                                    public void onClick(DialogInterface dialog, int which) {
//                                                                                                        // continue
//
//
//
//                                                                                                    }
//                                                                                                })
//                                                                                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                                                                                                    public void onClick(DialogInterface dialog, int which) {
//                                                                                                        // do nothing
//                                                                                                    }
//                                                                                                })
//                                                                                                .show();
//                                                                                        Lend_detailsFragment ldf = new Lend_detailsFragment();
//                                                                                        Bundle args = new Bundle();
//                                                                                        args.putString("id", id);
//                                                                                        ldf.setArguments(args);
//                                                                                        getFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).commit();

                                                                                    }
                                                                                });


                                                                            }
                                                                        }

                                                                    } catch (IOException e) {
                                                                        Toast.makeText(getContext(),""+e,Toast.LENGTH_LONG).show();
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                }
                                                            });


                                                        }
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }

                                    }
                                }

                            } catch (IOException e) {
                                Toast.makeText(getContext(),""+e,Toast.LENGTH_LONG).show();
                            }

                        } else {
                            // cant find location
                            Toast.makeText(getContext(), "can't find location", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Toast.makeText(getContext(), "" + e, Toast.LENGTH_LONG).show();
        }

    }

    private void moveCamera(LatLng latLng, float zoom) {

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

    }

    private void initMap() {
        try {
            FragmentManager fm = getChildFragmentManager();
            SupportMapFragment mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        } catch (Exception e) {
            Toast.makeText(getContext(), "" + e, Toast.LENGTH_LONG).show();
        }
    }

//    private void getLocationPermission() {
//
//        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};
//
//        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//
//            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//
//                mLocationPermissionGranted = true;
//
//                initMap();
//
//            } else {
//
//                ActivityCompat.requestPermissions(getActivity(), permissions, LOCATION_PERMISSION_REQUEST_CODE);
//
//            }
//        } else {
//
//            ActivityCompat.requestPermissions(getActivity(), permissions, LOCATION_PERMISSION_REQUEST_CODE);
//
//        }
//
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//
//        mLocationPermissionGranted = false;
//
//        switch (requestCode) {
//
//            case LOCATION_PERMISSION_REQUEST_CODE: {
//
//                if (grantResults.length > 0) {
//
//                    for (int i = 0; i < grantResults.length; i++) {
//
//                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
//                            mLocationPermissionGranted = false;
//                            return;
//                        }
//
//                    }
//
//                    mLocationPermissionGranted = true;
//
//                    //initialize our map
//                    initMap();
//                }
//            }
//        }
//    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        if (mLocationPermissionGranted) {

            getDeviceLocation();

//            if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                    ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                return;
//            }
//
//            mMap.setMyLocationEnabled(true);
//            mMap.getUiSettings().isCompassEnabled();
//            mMap.getUiSettings().isZoomControlsEnabled();
//            mMap.getUiSettings().setAllGesturesEnabled(true);

//            LatLng sydney = new LatLng(-34, 151);
//            mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney").snippet("Consider yourself located"));
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

            // get nearby

//            mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
//                @Override
//                public boolean onMyLocationButtonClick() {
//                    getDeviceLocation();
//                    return false;
//                }
//            });

        }

    }
}

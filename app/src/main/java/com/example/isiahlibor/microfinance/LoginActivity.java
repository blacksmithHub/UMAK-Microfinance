package com.example.isiahlibor.microfinance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sqlitelib.DataBaseHelper;
import com.sqlitelib.SQLite;

import java.util.HashMap;


public class LoginActivity extends AppCompatActivity implements ExampleDialog.ExampleDialogListener {

    EditText user, password;
    Button login;
    TextView signup;
    int id;
    String checkEmail, checkUser, checkPass;
    Intent intent;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        intent = new Intent(LoginActivity.this, MainActivity.class);
        // if user is current logged in, redirect to main activity
        if(firebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(intent);
        }
        // loading
        progressDialog = new ProgressDialog(this);

        user = (EditText)findViewById(R.id.user);
        password = (EditText)findViewById(R.id.password);
        login = (Button)findViewById(R.id.login);
        signup = (TextView)findViewById(R.id.signup);

        login();
        signup();

    }

    // sign up button
    public void signup(){

    signup.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            ExampleDialog exampleDialog = new ExampleDialog();
            exampleDialog.show(getSupportFragmentManager(), "example dialog");

        }
    });

    }

    // login button
    private void login(){

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // loading
                progressDialog.setMessage("Signing in...");
                progressDialog.show();
                // get input fields
                checkEmail = user.getText().toString().trim();
                checkPass = password.getText().toString().trim();
                // create user authentication
                firebaseAuth.signInWithEmailAndPassword(checkEmail, checkPass).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // dismiss loading
                        progressDialog.dismiss();

                        if(task.isSuccessful()){
                            finish();
                            // launch main activity
                            startActivity(intent);
                        }

                    }
                });

            }
        });

    }

    // register
    @Override
    public void applyTexts(final String fname, final String lname, final String mail, final String psw, final String gender) {

        // loading
        progressDialog.setMessage("Signing up...");
        progressDialog.show();

        // register user
        firebaseAuth.createUserWithEmailAndPassword(mail,psw).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    // get auth()->user()->id
                    String user_id = user.getUid();

                    // create table user
                    database = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
                    // create columns with values
                    HashMap<String, String> userMap = new HashMap<>();
                    userMap.put("user_id", user_id);
                    userMap.put("firstname", fname);
                    userMap.put("lastname", lname);
                    userMap.put("email", mail);
                    userMap.put("password", psw);
                    userMap.put("gender", gender);
                    userMap.put("latitude", "none");
                    userMap.put("longitude", "none");

                    database.setValue(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // successfully created table user
                            progressDialog.dismiss();
                        }
                    });

                    // create table stores
                    database = FirebaseDatabase.getInstance().getReference().child("Stores").child(user_id);
                    // create columns with values, set to 'none' for default
                    HashMap<String, String> storeMap = new HashMap<>();
                    storeMap.put("user_id", user_id);
                    storeMap.put("title", "none");
                    storeMap.put("address", "none");

                    database.setValue(storeMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // successfully created table user
                            progressDialog.dismiss();
                        }
                    });

                    // create table goals
                    database = FirebaseDatabase.getInstance().getReference().child("Goals").child(user_id);
                    // create columns with values, set to 'none' for default
                    HashMap<String, String> goalMap = new HashMap<>();
                    goalMap.put("user_id", user_id);
                    goalMap.put("amount", "none");
                    goalMap.put("rate", "none");
                    goalMap.put("status", "none");

                    database.setValue(goalMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // successfully created table user
                            progressDialog.dismiss();
                        }
                    });

                }
            }
        });

    }
}

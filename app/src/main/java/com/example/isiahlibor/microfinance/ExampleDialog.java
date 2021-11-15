package com.example.isiahlibor.microfinance;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

public class ExampleDialog extends AppCompatDialogFragment {
    private EditText editTextFirstname, editTextLastname, editTextPassword, editTextEmail;
    private ExampleDialogListener listener;
    private Spinner gender;
    String selected;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog,null);
        builder.setView(view)
        .setTitle("Register")
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        })
                .setPositiveButton("Sign up", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    String psw = editTextPassword.getText().toString();
                    String fname = editTextFirstname.getText().toString();
                    String lname = editTextLastname.getText().toString();
                    String mail = editTextEmail.getText().toString();
                    listener.applyTexts(fname,lname, mail, psw, selected);
                    }
                });
        editTextPassword = view.findViewById(R.id.edit_password);
        editTextFirstname = view.findViewById(R.id.edit_firstname);
        editTextLastname = view.findViewById(R.id.edit_lastname);
        editTextEmail = view.findViewById(R.id.edit_email);
        gender = view.findViewById(R.id.gender);
        getGender();
    return builder.create();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (ExampleDialogListener) context;
        }catch(ClassCastException e){
            throw new ClassCastException(context.toString() + "must implement exampledialoglistener");
        }
    }

    public interface ExampleDialogListener{
        void applyTexts(String fname, String lname, String mail, String psw, String gender);
    }

    public void getGender(){
        gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected = gender.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}

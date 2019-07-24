package com.example.ayomide.atsnote;

import android.support.design.widget.FloatingActionButton;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.ayomide.atsnote.Model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Help extends AppCompatActivity {

    FloatingActionButton fab;

    User currentUser;

    FirebaseDatabase db;
    DatabaseReference table_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_help );

        db = FirebaseDatabase.getInstance();
        table_user = db.getReference( "User" );

        fab = findViewById( R.id.phone_help );
        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeCall();
            }
        } );
    }

    private void makeCall() {
        //...
    }
}

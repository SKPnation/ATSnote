package com.example.ayomide.atsnote;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.ayomide.atsnote.Common.Common;
import com.example.ayomide.atsnote.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Help extends AppCompatActivity {

    FloatingActionButton fab;

    User user;

    FirebaseDatabase db;
    DatabaseReference table_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_help );

        db = FirebaseDatabase.getInstance();
        table_user = db.getReference("User");

        fab = findViewById( R.id.phone_help );
        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeCall();
            }
        } );
    }

    private void makeCall()
    {
        //...
    }
}

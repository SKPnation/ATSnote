package com.example.ayomide.atsnote;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.ayomide.atsnote.Common.Common;
import com.example.ayomide.atsnote.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class StartActivity extends AppCompatActivity {

    DatabaseReference table_user;

    ImageButton enter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_start );

        //Init paper
        Paper.init(this);

        enter = findViewById( R.id.enter );

        enter.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signIn = new Intent(StartActivity.this, SignIn.class);
                startActivity(signIn);
                finish();
            }
        } );
        //check remember
        String user = Paper.book().read(Common.USER_KEY);
        String pwd = Paper.book().read(Common.PWD_KEY);
        if(user != null && pwd != null)
        {
            if(!user.isEmpty() && !pwd.isEmpty())
                login(user, pwd);
        }
    }

    private void login(final String phone, final String pwd)
    {
        table_user = FirebaseDatabase.getInstance().getReference("User");

        //just copy login code from Signin activity
        if (Common.isConnectedToTheInternet(getBaseContext())) {
            //function to save user & password is checkbox is checked


            final ProgressDialog mDialog = new ProgressDialog( StartActivity.this );
            mDialog.setMessage( "Loading..." );
            mDialog.show();

            if (TextUtils.isEmpty( phone )) {
                Toast.makeText( StartActivity.this, "Please enter phone number...", Toast.LENGTH_SHORT ).show();
            }
            if (TextUtils.isEmpty( pwd )) {
                Toast.makeText( StartActivity.this, "Please enter password...", Toast.LENGTH_SHORT ).show();
            } else {
                table_user.addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        //check if user exists in database
                        if (dataSnapshot.child( phone ).exists()) {
                            //Get User info
                            mDialog.dismiss();

                            User user = dataSnapshot.child( phone ).getValue( User.class );

                            if (user.getPassword().equals( pwd )) {

                                Intent homeIntent = new Intent( StartActivity.this, MainActivity.class );
                                Common.currentUser = user;
                                startActivity( homeIntent );
                                finish();
                                Toast.makeText( StartActivity.this, "Welcome back " + Common.currentUser.getName(), Toast.LENGTH_LONG ).show();

                            } else {
                                Toast.makeText( StartActivity.this, "Incorrect Password", Toast.LENGTH_LONG ).show();
                            }
                        } else {
                            mDialog.dismiss();
                            Toast.makeText( StartActivity.this, "Baba, you no get account", Toast.LENGTH_LONG ).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                } );
            }
        }
        else
            Toast.makeText(StartActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
        return;
    }


}

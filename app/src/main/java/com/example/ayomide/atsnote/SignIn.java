package com.example.ayomide.atsnote;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ayomide.atsnote.Common.Common;
import com.example.ayomide.atsnote.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import io.paperdb.Paper;

public class SignIn extends AppCompatActivity {

    MaterialEditText etPhone, etPassword;
    CheckBox remember_me;
    TextView forgot_pwd;
    Button btnSignIn;
    CheckBox cbRemember;

    private ProgressDialog mDialog;

    FirebaseDatabase db;
    DatabaseReference table_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_sign_in );

        db = FirebaseDatabase.getInstance();
        table_user = db.getReference("User");

        etPhone = findViewById( R.id.etPhone );
        etPassword = findViewById( R.id.etPassword );
        remember_me = findViewById( R.id.cbRemember );
        forgot_pwd = findViewById( R.id.tvForgotPass );
        cbRemember = findViewById( R.id.cbRemember );

        btnSignIn = findViewById(R.id.btnSignIn);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginAdminUser();
            }
        });

        forgot_pwd.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showForgotPwdDialog();
            }
        } );
    }

    private void loginAdminUser()
    {
        if (Common.isConnectedToTheInternet( getBaseContext() )) {

            if(cbRemember.isChecked())
            {
                Paper.book().write( Common.USER_KEY, etPhone.getText().toString() );
                Paper.book().write( Common.PWD_KEY, etPassword.getText().toString() );
            }

            mDialog = new ProgressDialog( SignIn.this );
            mDialog.setMessage( "Loading..." );
            mDialog.show();

            table_user.addValueEventListener( new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child( etPhone.getText().toString() ).exists()) {
                        mDialog.dismiss();
                        User user = dataSnapshot.child( etPhone.getText().toString() ).getValue( User.class );
                        user.setPhone( etPhone.getText().toString() );

                        if (Boolean.parseBoolean( user.getIsStaff() )) {

                            if (user.getPassword().equals( etPassword.getText().toString() )) {

                                Intent homeIntent = new Intent( SignIn.this, MainActivity.class );
                                Common.currentUser = user;
                                startActivity( homeIntent );
                                finish();
                                Toast.makeText( SignIn.this, "Login successful", Toast.LENGTH_SHORT ).show();

                            } else {
                                Toast.makeText( SignIn.this, "Wrong password", Toast.LENGTH_SHORT ).show();
                            }
                        } else {
                            Toast.makeText( SignIn.this, "Please login with staff account", Toast.LENGTH_SHORT ).show();
                        }
                    } else {
                        mDialog.dismiss();
                        Toast.makeText( SignIn.this, "User does not exist", Toast.LENGTH_SHORT ).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            } );
        }
    }

    private void showForgotPwdDialog()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(SignIn.this);
        builder.setTitle("Forgot Password");
        builder.setMessage("Enter your security code");

        LayoutInflater inflater = this.getLayoutInflater();
        View forgot_view = inflater.inflate(R.layout.forgot_password_layout, null);

        builder.setView(forgot_view);
        builder.setIcon(R.drawable.ic_security_black_24dp);

        final MaterialEditText etPhone = forgot_view.findViewById(R.id.etPhone);
        final MaterialEditText etSecurityCode = forgot_view.findViewById(R.id.etSecurityCode);

        builder.setPositiveButton( "YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Check if user is available
                table_user.addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.child(etPhone.getText().toString()).getValue(User.class);

                        if(user.getSecurityCode().equals(etSecurityCode.getText().toString()))
                            Toast.makeText(SignIn.this, "Your password is: "+user.getPassword(), Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(SignIn.this, "Wrong security code", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        builder.setNegativeButton( "NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.show();
    }

}

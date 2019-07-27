package com.example.ayomide.atsnote;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.example.ayomide.atsnote.Common.Common;
import com.example.ayomide.atsnote.Model.Parent;
import com.example.ayomide.atsnote.ViewHolder.ParentViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

public class ParentManagement extends AppCompatActivity {

    FloatingActionButton fabAdd;

    MaterialEditText etName, etPhone, etPassword;

    FirebaseDatabase database;
    DatabaseReference parents;

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Parent, ParentViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_parent_management );

        database = FirebaseDatabase.getInstance();
        parents = database.getReference(Common.PARENTS_TABLE );

        fabAdd = (FloatingActionButton) findViewById( R.id.fab );
        fabAdd.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreateParentDialog();
            }
        } );

        recyclerView = findViewById( R.id.recycler_parents );
        recyclerView.setHasFixedSize( true );
        layoutManager = new LinearLayoutManager( this);
        recyclerView.setLayoutManager( layoutManager );

        loadParents();
    }

    private void loadParents()
    {
        adapter = new FirebaseRecyclerAdapter<Parent, ParentViewHolder>(
                Parent.class,
                R.layout.parent_layout,
                ParentViewHolder.class,
                parents) {
            @Override
            protected void populateViewHolder(ParentViewHolder viewHolder, final Parent model, final int position) {
                viewHolder.parent_name.setText( model.getName() );
                viewHolder.parent_phone.setText( model.getPhone() );

                viewHolder.btn_edit.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showEditDialog(adapter.getRef( position ).getKey(), adapter.getItem( position ));
                    }
                } );

                viewHolder.btn_delete.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder( ParentManagement.this );
                        alertDialog.setTitle( "Are you sure?" );

                        alertDialog.setPositiveButton( "YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();

                                parents.child(adapter.getRef( position ).getKey()).removeValue();
                                Toast.makeText( ParentManagement.this, "Deleted "+model.getName(), Toast.LENGTH_SHORT ).show();
                            }
                        } );

                        alertDialog.setNegativeButton( "NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        } );

                        alertDialog.show();
                    }
                } );
            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter( adapter );
    }

    private void showEditDialog(final String key, final Parent item)
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder( ParentManagement.this );
        alertDialog.setTitle( "Edit Parent's Info" );
        alertDialog.setIcon( R.drawable.ic_person_black_24dp );

        LayoutInflater inflater = this.getLayoutInflater();
        View edit_parent_layout = inflater.inflate( R.layout.add_new_parent_layout, null );

        etName = edit_parent_layout.findViewById( R.id.etParentName );
        etPhone = edit_parent_layout.findViewById( R.id.etParentPhone );
        etPassword = edit_parent_layout.findViewById( R.id.etParentPassword );

        etName.setText( item.getName() );
        etPhone.setText( item.getPhone() );
        etPassword.setText( item.getPassword() );

        alertDialog.setView( edit_parent_layout );

        alertDialog.setPositiveButton( "DONE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                item.setName( etName.getText().toString() );
                item.setPhone( etPhone.getText().toString() );
                item.setPassword( etPassword.getText().toString() );

                parents.child( key ).setValue( item );
            }
        } );

        alertDialog.setNegativeButton( "CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        } );

        alertDialog.show();

    }

    private void showCreateParentDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder( ParentManagement.this );
        builder.setTitle( "Create Parent" );
        builder.setIcon( R.drawable.ic_person_add_black_24dp );

        LayoutInflater inflater = this.getLayoutInflater();
        View add_new_parent = inflater.inflate( R.layout.add_new_parent_layout, null );

        etName = add_new_parent.findViewById( R.id.etParentName );
        etPhone = add_new_parent.findViewById( R.id.etParentPhone );
        etPassword = add_new_parent.findViewById( R.id.etParentPassword );

        builder.setView( add_new_parent );

        builder.setPositiveButton( "DONE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (TextUtils.isEmpty( etName.getText().toString() )
                        || TextUtils.isEmpty( etPhone.getText().toString() )
                        || TextUtils.isEmpty( etPassword.getText().toString() ))
                {
                    Toast.makeText( ParentManagement.this, "All fields are required", Toast.LENGTH_SHORT ).show();
                }
                else
                    {
                        Parent parent = new Parent();
                        parent.setName( etName.getText().toString() );
                        parent.setPhone( etPhone.getText().toString() );
                        parent.setPassword( etPassword.getText().toString() );

                        parents.child( etPhone.getText().toString() )
                                .setValue( parent )
                                .addOnSuccessListener( new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText( ParentManagement.this, "Parent created successfully", Toast.LENGTH_SHORT ).show();
                                    }
                                } ).addOnFailureListener( new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText( ParentManagement.this, e.getMessage(), Toast.LENGTH_SHORT ).show();
                            }
                        } );
                    }
            }
        } );

        builder.show();
    }
}

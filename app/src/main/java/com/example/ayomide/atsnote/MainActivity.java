package com.example.ayomide.atsnote;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ayomide.atsnote.Common.Common;
import com.example.ayomide.atsnote.Interface.ItemClickListener;
import com.example.ayomide.atsnote.Model.Category;
import com.example.ayomide.atsnote.Model.Pupil;
import com.example.ayomide.atsnote.ViewHolder.ClassViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.UUID;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //Firebase
    FirebaseDatabase db;
    DatabaseReference category;
    FirebaseRecyclerAdapter<Category, ClassViewHolder> adapter;

    //Add new menu layout
    MaterialEditText etName;

    Category newCategory;

    //View
    RecyclerView recycler_grade;
    RecyclerView.LayoutManager layoutManager;

    TextView tvFullName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        toolbar.setTitle("Class Management");
        setSupportActionBar( toolbar );

        db = FirebaseDatabase.getInstance();
        category = db.getReference("Class");

        //Init Paper
        Paper.init( this );

        FloatingActionButton fab = (FloatingActionButton) findViewById( R.id.fab );
        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               showDialog();
            }
        } );

        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );
        drawer.addDrawerListener( toggle );
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById( R.id.nav_view );
        navigationView.setNavigationItemSelectedListener( this );

        //Set Name for user
        View headerView = navigationView.getHeaderView(0);
        tvFullName = (TextView) headerView.findViewById(R.id.tvFullName);
        tvFullName.setText(Common.currentUser.getName());

        //Load classes
        recycler_grade = (RecyclerView) findViewById(R.id.recycler_grades);
        recycler_grade.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_grade.setLayoutManager(layoutManager);

        loadClasses();
    }

    private void showDialog()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Add new Class");
        alertDialog.setMessage("Put in full information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_class_layout, null);

        etName = add_menu_layout.findViewById(R.id.etName);

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_playlist_add_black_24dp);

        //set button
        alertDialog.setPositiveButton( "YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                final String key = UUID.randomUUID().toString();
                DatabaseReference reference = category;
                reference.child( key ).setValue( etName.getText().toString() ).addOnCompleteListener( new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            Toast.makeText( MainActivity.this, "File successfully uploaded", Toast.LENGTH_SHORT ).show();
                            newCategory = new Category(etName.getText().toString());

                            category.child( key ).setValue( newCategory );
                        }
                    }
                } );

            }
        });

        alertDialog.setNegativeButton( "NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.show();
    }

    private void loadClasses()
    {
        adapter = new FirebaseRecyclerAdapter<Category, ClassViewHolder>(
                Category.class, R.layout.grade_layout, ClassViewHolder.class, category) {
            @Override
            protected void populateViewHolder(ClassViewHolder viewHolder, Category model, int position) {
                viewHolder.tvClassName.setText( model.getName() );

                viewHolder.setItemClickListener( new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //send classId to new Activity and start new Activity
                        Intent pupilList = new Intent( MainActivity.this, PupilsList.class );
                        //Because classId is key so we just get key of this items
                        pupilList.putExtra( "ClassId", adapter.getRef( position ).getKey() );
                        startActivity( pupilList );
                    }
                } );
            }
        };
        adapter.notifyDataSetChanged();
        recycler_grade.setAdapter( adapter );
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        if (drawer.isDrawerOpen( GravityCompat.START )) {
            drawer.closeDrawer( GravityCompat.START );
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.main, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            //Delete remembered user and password
            Paper.book().destroy();

            Intent signIn = new Intent(MainActivity.this, SignIn.class);
            signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signIn);

            return true;
        }

        return super.onOptionsItemSelected( item );
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_classes) {
            // Handle the class action
        } else if (id == R.id.nav_help) {
            Intent helpIntent = new Intent( MainActivity.this, Help.class );
            startActivity( helpIntent );
        }

        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        drawer.closeDrawer( GravityCompat.START );
        return true;
    }

    //update / delete

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if(item.getTitle().equals(Common.EDIT))
        {
            showEditDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        }
        else if(item.getTitle().equals(Common.DELETE))
        {
            deleteCategory(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        }
        return super.onContextItemSelected(item);
    }

    private void deleteCategory(String key, final Category item)
    {
        DatabaseReference pupils = FirebaseDatabase.getInstance().getReference("Pupil");
        Query pupilInClass = pupils.orderByChild("pupilId").equalTo(key);
        pupilInClass.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot:dataSnapshot.getChildren())
                {
                    postSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

        category.child(key).removeValue();
        Toast.makeText(MainActivity.this, item.getName()+" deleted!!!", Toast.LENGTH_LONG ).show();
    }

    private void showEditDialog(final String key, final Category item)
    {
        //just copy code from showDialog and modify
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Update Category");
        alertDialog.setMessage("Put in full information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_class_layout, null);

        etName = add_menu_layout.findViewById(R.id.etName);

        etName.setText(item.getName());

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_class_black_24dp);

        //set button
        alertDialog.setPositiveButton( "YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                item.setName(etName.getText().toString());
                category.child(key).setValue(item);
            }
        });

        alertDialog.setNegativeButton( "NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.show();
    }

}

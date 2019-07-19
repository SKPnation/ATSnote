package com.example.ayomide.atsnote;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ayomide.atsnote.Common.Common;
import com.example.ayomide.atsnote.Model.Category;
import com.example.ayomide.atsnote.Model.Pupil;
import com.example.ayomide.atsnote.Model.User;
import com.example.ayomide.atsnote.ViewHolder.ClassViewHolder;
import com.example.ayomide.atsnote.ViewHolder.PupilViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class PupilsList extends AppCompatActivity {

    //Firebase
    FirebaseDatabase db;
    DatabaseReference pupilsList;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseRecyclerAdapter<Pupil, PupilViewHolder> adapter;

    String classId = "";

    //View
    RecyclerView recycler_pupils;
    RecyclerView.LayoutManager layoutManager;

    FloatingActionButton fab;

    //Add new pupil layout
    EditText etName, etAge, etGrade, etEntryCode, etHomeAddress, etPhone, etGname, etGemail, etOfficeAddress;
    Button btnSelect, btnUpload, btnPdfSelect, btnPdfUpload;
    TextView tvReportFile;

    Pupil newPupil;

    Uri saveUri, pdfUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_pupils_list );

        db = FirebaseDatabase.getInstance();
        pupilsList = db.getReference( "Pupil" );
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //load Pupils list
        recycler_pupils = findViewById( R.id.recycler_pupils );
        recycler_pupils.setHasFixedSize( true );
        layoutManager = new LinearLayoutManager( this );
        recycler_pupils.setLayoutManager( layoutManager );

        fab = (FloatingActionButton) findViewById( R.id.fab );
        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNewPupilDialog();
            }
        } );

        if (getIntent() != null)
            classId = getIntent().getStringExtra( "ClassId" );
        if (!classId.isEmpty())
            loadPupilsList( classId );
    }

    private void loadPupilsList(String classId) {
        adapter = new FirebaseRecyclerAdapter<Pupil, PupilViewHolder>(
                Pupil.class,
                R.layout.pupil_layout,
                PupilViewHolder.class,
                pupilsList.orderByChild( "categoryId" ).equalTo( classId ) ) {
            @Override
            protected void populateViewHolder(PupilViewHolder viewHolder, final Pupil model, final int position) {
                viewHolder.pupil_name.setText( model.getName() );
                viewHolder.pupil_age.setText( model.getAge() );
                viewHolder.tvReportFile.setText( model.getReportPdf() );
                Picasso.with( getBaseContext() ).load( model.getImage() ).into( viewHolder.pupil_image );

                viewHolder.btnEdit.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showEditDialog( adapter.getRef( position ).getKey(), adapter.getItem( position ) );
                    }
                } );

                viewHolder.btnReport.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showPdfDialog( adapter.getRef( position ).getKey(), adapter.getItem( position ) );
                    }
                } );

                viewHolder.btnRemove.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder( PupilsList.this );
                        alertDialog.setTitle( "Are you sure?" );

                        alertDialog.setPositiveButton( "YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                DatabaseReference pupils = FirebaseDatabase.getInstance().getReference( "Pupil" );
                                Query pupilInClass = pupils.orderByChild( "categoryId" ).equalTo( adapter.getRef( position ).getKey() );
                                pupilInClass.addListenerForSingleValueEvent( new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                            postSnapshot.getRef().removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                } );

                                pupilsList.child( adapter.getRef( position ).getKey() ).removeValue();
                                Toast.makeText( PupilsList.this, "Deleted " + model.getName(), Toast.LENGTH_LONG ).show();
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
        recycler_pupils.setAdapter( adapter );
    }

    private void showPdfDialog(final String key, final Pupil item) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder( PupilsList.this );
        alertDialog.setTitle( "Upload Report Card" );

        LayoutInflater inflater = this.getLayoutInflater();
        View upload_report_layout = inflater.inflate( R.layout.pdf_upload_layout, null );

        tvReportFile = upload_report_layout.findViewById( R.id.report_file );
        btnPdfSelect = upload_report_layout.findViewById( R.id.btnPdfSelect );
        btnPdfUpload = upload_report_layout.findViewById( R.id.btnPdfUpload );

        tvReportFile.setText( item.getReportPdf() );

        btnPdfSelect.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission( PupilsList.this, Manifest.permission.READ_EXTERNAL_STORAGE ) == PackageManager.PERMISSION_DENIED) {
                    selectPdf();
                } else
                    ActivityCompat.requestPermissions( PupilsList.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9 );
            }
        } );

        btnPdfUpload.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFile( key, item );
            }
        } );

        alertDialog.setView( upload_report_layout );
        alertDialog.setIcon( R.drawable.ic_file_upload_black_24dp );

        alertDialog.setPositiveButton( "DONE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
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

    private void selectPdf() {
        //to allow user to select a file using file manager, we will use intent

        Intent intent = new Intent();
        intent.setType( "application/pdf" );
        intent.setAction( Intent.ACTION_GET_CONTENT );
        startActivityForResult( intent, Common.PDF_REQUEST );
    }

    private void uploadFile(final String key, final Pupil item) {
        if (pdfUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog( PupilsList.this );
            progressDialog.setProgressStyle( ProgressDialog.STYLE_HORIZONTAL );
            progressDialog.setTitle( "Uploading file..." );
            progressDialog.setProgress( 0 );
            progressDialog.show();

            final String fileName = UUID.randomUUID().toString();
            final StorageReference reportFolder = storageReference.child( "reportFiles/" + fileName );
            reportFolder.putFile( pdfUri ).addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(final Uri uri) {
                            final String url = uri.toString();
                            DatabaseReference reference = pupilsList;
                            reference.child( fileName ).setValue( url ).addOnCompleteListener( new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        progressDialog.dismiss();
                                        Toast.makeText( PupilsList.this, "File successfully uploaded", Toast.LENGTH_SHORT ).show();
                                        item.setReportPdf( url );

                                        pupilsList.child( key ).setValue( item );
                                    }
                                    else
                                        Toast.makeText( PupilsList.this, "File not successfully uploaded", Toast.LENGTH_SHORT ).show();
                                }
                            } );
                        }
                    } );
                }
            } ).addOnFailureListener( new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText( PupilsList.this, "File not successfully uploaded", Toast.LENGTH_SHORT ).show();
                }
            } ).addOnProgressListener( new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    int currentProgress = (int) (100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    progressDialog.setProgress( currentProgress );
                }
            } );
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 9 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectPdf();
        } else
            Toast.makeText( PupilsList.this, "Please provide permission..", Toast.LENGTH_SHORT ).show();
    }


    private void showEditDialog(final String key, final Pupil item) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder( PupilsList.this );
        alertDialog.setTitle( "Edit Pupil's Info" );

        LayoutInflater inflater = this.getLayoutInflater();
        View edit_pupil_layout = inflater.inflate( R.layout.add_new_pupil_layout, null );

        etName = edit_pupil_layout.findViewById( R.id.etName );
        etAge = edit_pupil_layout.findViewById( R.id.etAge );
        etGrade = edit_pupil_layout.findViewById( R.id.etGrade );
        etEntryCode = edit_pupil_layout.findViewById( R.id.etEntryCode );
        etHomeAddress = edit_pupil_layout.findViewById( R.id.etHomeAddress );
        etPhone = edit_pupil_layout.findViewById( R.id.etPhone );
        etGname = edit_pupil_layout.findViewById( R.id.etGuardianName );
        etGemail = edit_pupil_layout.findViewById( R.id.etGuardianEmail );
        etOfficeAddress = edit_pupil_layout.findViewById( R.id.etOfficeAddress );
        btnSelect = edit_pupil_layout.findViewById( R.id.btnSelect );
        btnUpload = edit_pupil_layout.findViewById( R.id.btnUpload );

        etName.setText( item.getName() );
        etAge.setText( item.getAge() );
        etGrade.setText( item.getGrade() );
        etEntryCode.setText( item.getEntryCode() );
        etHomeAddress.setText( item.getAddress() );
        etPhone.setText( item.getPhone() );
        etGname.setText( item.getgName() );
        etGemail.setText( item.getgEmail() );
        etOfficeAddress.setText( item.getgOfficeAddress() );

        btnSelect.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        } );

        btnUpload.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeImage( item );
            }
        } );

        alertDialog.setView( edit_pupil_layout );
        alertDialog.setIcon( R.drawable.ic_person_black_24dp );

        alertDialog.setPositiveButton( "DONE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                item.setName( etName.getText().toString() );
                item.setAge( etAge.getText().toString() );
                item.setGrade( etGrade.getText().toString() );
                item.setEntryCode( etEntryCode.getText().toString() );
                item.setAddress( etHomeAddress.getText().toString() );
                item.setPhone( etPhone.getText().toString() );
                item.setgName( etGname.getText().toString() );
                item.setgEmail( etGemail.getText().toString() );
                item.setgOfficeAddress( etOfficeAddress.getText().toString() );

                pupilsList.child( key ).setValue( item );
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

    private void showNewPupilDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder( PupilsList.this );
        alertDialog.setTitle( "Add New Pupil" );
        alertDialog.setMessage( "Put in full information" );
        alertDialog.setIcon( R.drawable.ic_person_add_black_24dp );

        LayoutInflater layoutInflater = this.getLayoutInflater();
        View view = layoutInflater.inflate( R.layout.add_new_pupil_layout, null );

        etName = view.findViewById( R.id.etName );
        etAge = view.findViewById( R.id.etAge );
        etGrade = view.findViewById( R.id.etGrade );
        etEntryCode = view.findViewById( R.id.etEntryCode );
        etHomeAddress = view.findViewById( R.id.etHomeAddress );
        etPhone = view.findViewById( R.id.etPhone );
        etGname = view.findViewById( R.id.etGuardianName );
        etGemail = view.findViewById( R.id.etGuardianEmail );
        etOfficeAddress = view.findViewById( R.id.etOfficeAddress );

        btnSelect = view.findViewById( R.id.btnSelect );
        btnUpload = view.findViewById( R.id.btnUpload );

        btnSelect.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        } );

        btnUpload.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        } );

        alertDialog.setView( view );

        alertDialog.setPositiveButton( "YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Here create new category
                if (newPupil != null) {
                    pupilsList.push().setValue( newPupil );
                    Toast.makeText( PupilsList.this, "New pupil " + newPupil.getName() + " was added", Toast.LENGTH_SHORT )
                            .show();
                }
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

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType( "image/*" );
        intent.setAction( Intent.ACTION_GET_CONTENT );
        startActivityForResult( Intent.createChooser( intent, "Select A Profile Picture" ), Common.IMAGE_REQUEST );
    }

    private void uploadImage() {
        if (saveUri != null) {
            final ProgressDialog mDialog = new ProgressDialog( this );
            mDialog.setMessage( "Uploading..." );
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child( "profileImages/" + imageName );
            imageFolder.putFile( saveUri )
                    .addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText( PupilsList.this, "Uploaded!!!", Toast.LENGTH_SHORT ).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    newPupil = new Pupil();
                                    newPupil.setName( etName.getText().toString() );
                                    newPupil.setAge( etName.getText().toString() );
                                    newPupil.setGrade( etGrade.getText().toString() );
                                    newPupil.setEntryCode( etEntryCode.getText().toString() );
                                    newPupil.setAddress( etHomeAddress.getText().toString() );
                                    newPupil.setPhone( etPhone.getText().toString() );
                                    newPupil.setgName( etGname.getText().toString() );
                                    newPupil.setgEmail( etGemail.getText().toString() );
                                    newPupil.setgOfficeAddress( etOfficeAddress.getText().toString() );
                                    newPupil.setCategoryId( classId );
                                    newPupil.setImage( uri.toString() );
                                }
                            } );
                        }
                    } )
                    .addOnFailureListener( new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText( PupilsList.this, "" + e.getMessage(), Toast.LENGTH_SHORT ).show();
                        }
                    } )
                    .addOnProgressListener( new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage( "Uploaded " + progress + "%" );
                        }
                    } );
        }
    }

    private void changeImage(final Pupil item) {
        if (saveUri != null) {
            final ProgressDialog mDialog = new ProgressDialog( this );
            mDialog.setMessage( "Uploading..." );
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child( "profileImages/" + imageName );
            imageFolder.putFile( saveUri )
                    .addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText( PupilsList.this, "Uploaded!!!", Toast.LENGTH_SHORT ).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    item.setImage( uri.toString() );
                                }
                            } );
                        }
                    } )
                    .addOnFailureListener( new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText( PupilsList.this, "" + e.getMessage(), Toast.LENGTH_LONG ).show();
                        }
                    } )
                    .addOnProgressListener( new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage( "Uploaded " + progress + "%" );
                        }
                    } );
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

        if (requestCode == Common.IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            saveUri = data.getData();
            btnSelect.setText( "Image Selected" );
        }

        if (requestCode == Common.PDF_REQUEST && resultCode == RESULT_OK && data != null) {
            pdfUri = data.getData(); //return the uri of the selected file
            tvReportFile.setText( "the report file link : " + data.getData().getLastPathSegment() );
        } else {
            Toast.makeText( PupilsList.this, "Please select a file", Toast.LENGTH_SHORT ).show();
        }
    }
}
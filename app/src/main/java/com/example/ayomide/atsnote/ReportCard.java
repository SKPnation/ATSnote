package com.example.ayomide.atsnote;

import android.net.http.SslError;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ayomide.atsnote.Common.Common;
import com.example.ayomide.atsnote.Interface.ItemClickListener;
import com.example.ayomide.atsnote.Interface.ItemClickListenerII;
import com.example.ayomide.atsnote.Model.Pupil;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ReportCard extends AppCompatActivity{

    FirebaseDatabase db;
    DatabaseReference pupils;

    String pupilId = "";

    Pupil currentPupil;

    TextView report_url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_report_card );

        db = FirebaseDatabase.getInstance();
        pupils = db.getReference("Pupil");

        report_url = findViewById( R.id.repor_url );

        //Get Food Id from Intent
        if(getIntent() != null)
            pupilId = getIntent().getStringExtra("pupilId");
        if(!pupilId.isEmpty())
        {
            getReport(pupilId);
        }
    }

    private void getReport(String pupilId)
    {
        pupils.child( pupilId ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentPupil = dataSnapshot.getValue(Pupil.class);

                report_url.setText( currentPupil.getReportPdf() );

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /*
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu( menu, v, menuInfo );
        getMenuInflater().inflate( R.menu.context_menu_sort, menu );
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.upload_pdf:
                Toast.makeText( ReportCard.this, "SELECTED", Toast.LENGTH_SHORT ).show();
                return true;
            case R.id.delete_pdf:
                Toast.makeText( ReportCard.this, "SELECTED", Toast.LENGTH_SHORT ).show();
                return true;
                default:
                    return super.onContextItemSelected( item );
        }

    } */

    /*if(getIntent() != null)
            pupilId = getIntent().getStringExtra("PupilId");
        if(!pupilId.isEmpty())
            loadPdf(pupilId);*/



    //registerForContextMenu( uploadClick );
}

package com.example.ayomide.atsnote;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ayomide.atsnote.Common.Common;
import com.example.ayomide.atsnote.Model.Pupil;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ReportCard extends AppCompatActivity{

    FirebaseDatabase db;
    DatabaseReference pupils;

    String pupilId = "";

    Pupil currentPupil;

    PDFView pdfView;

    TextView report_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_report_card );

        db = FirebaseDatabase.getInstance();
        pupils = db.getReference("Pupil");

        report_url = findViewById( R.id.repor_url );
        report_url.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail();
            }
        } );

        pdfView = findViewById( R.id.pdfView );

        //Get pupil Id from Intent
        if(getIntent() != null)
            pupilId = getIntent().getStringExtra("pupilId");
        if(!pupilId.isEmpty())
        {
            if (Common.isConnectedToTheInternet(getBaseContext()))
            {
                getReport( pupilId );
                Toast.makeText( ReportCard.this, "Tap the link above if you want to share the child's report card", Toast.LENGTH_LONG ).show();
            }
        }
    }

    private void sendEmail()
    {
        String subject = currentPupil.getName()+"'s report card";
        String text = currentPupil.getReportPdf();
        Intent intent = new Intent();
        intent.setType( "message/rfc2822" ); //message/rfc2822 is a mime type for email messages
        intent.setAction(  Intent.ACTION_SEND  );
        intent.putExtra( Intent.EXTRA_SUBJECT, subject );
        intent.putExtra( Intent.EXTRA_TEXT, text );
        startActivity(Intent.createChooser(intent, "Share using"));
    }

    private void getReport(String pupilId)
    {
        pupils.child( pupilId ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentPupil = dataSnapshot.getValue(Pupil.class);

                report_url.setText( currentPupil.getReportPdf() );
                //This function reads pdf from URL
                new RetrievePDFStream().execute( report_url.getText().toString() );

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }


    class RetrievePDFStream extends AsyncTask<String,Void,InputStream> {
        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;
            try{
                URL url = new URL( strings[0] );
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                if (urlConnection.getResponseCode()==200)
                {
                    inputStream = new BufferedInputStream( urlConnection.getInputStream() );
                }
            }
            catch (IOException e)
            {
                return null;
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            pdfView.fromStream( inputStream ).load();
        }
    }
}

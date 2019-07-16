package com.example.ayomide.atsnote;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.ayomide.atsnote.Model.Pupil;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ReportCard extends AppCompatActivity {

    FirebaseDatabase db;
    DatabaseReference pupils;

    String pupilId = "";

    Pupil currentPupil;

    PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_report_card );

        db = FirebaseDatabase.getInstance();
        pupils = db.getReference("Pupil");

        pdfView = findViewById( R.id.pdfview );

        if(getIntent() != null)
            pupilId = getIntent().getStringExtra("PupilId");
        if(!pupilId.isEmpty())
            loadPdf(pupilId) ;
    }

    private void loadPdf(String pupilId)
    {

    }
}

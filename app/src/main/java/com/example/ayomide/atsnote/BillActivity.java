package com.example.ayomide.atsnote;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ayomide.atsnote.Model.Pupil;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BillActivity extends AppCompatActivity {

    FirebaseDatabase db;
    DatabaseReference pupils;

    String pupilId = "";

    Pupil currentPupil;

    TextView bill_url;
    PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_bill );

        db = FirebaseDatabase.getInstance();
        pupils = db.getReference("Pupil");

        bill_url = findViewById( R.id.bill_url );
        bill_url.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //...
            }
        } );

        pdfView = findViewById( R.id.pdfView );

        //Get pupil Id from Intent
        if(getIntent() != null)
            pupilId = getIntent().getStringExtra("pupilId");
        if(!pupilId.isEmpty())
        {
            getReport(pupilId);
            Toast.makeText( BillActivity.this, "Tap the link above if you want to share the child's report card", Toast.LENGTH_LONG ).show();
        }
    }

    private void getReport(String pupilId)
    {
        //...
    }
}

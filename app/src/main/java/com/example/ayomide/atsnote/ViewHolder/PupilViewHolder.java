package com.example.ayomide.atsnote.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ayomide.atsnote.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class PupilViewHolder extends RecyclerView.ViewHolder {

    public CircleImageView pupil_image;
    public TextView pupil_name, pupil_age, tvReportFile;
    public Button btnEdit, btnReport, btnRemove;

    public PupilViewHolder(@NonNull View itemView) {
        super( itemView );

        pupil_image = itemView.findViewById( R.id.pupil_image );
        pupil_name = itemView.findViewById( R.id.pupil_name );
        pupil_age = itemView.findViewById( R.id.pupil_age );
        tvReportFile = itemView.findViewById( R.id.report_file );

        btnEdit = itemView.findViewById( R.id.btnEdit );
        btnReport = itemView.findViewById( R.id.btnReport );
        btnRemove = itemView.findViewById( R.id.btnRemove );
    }
}

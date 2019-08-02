package com.example.ayomide.atsnote.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ayomide.atsnote.Common.Common;
import com.example.ayomide.atsnote.Interface.ItemClickListener;
import com.example.ayomide.atsnote.PupilsList;
import com.example.ayomide.atsnote.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class PupilViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
        View.OnCreateContextMenuListener
{
    public CircleImageView pupil_image;
    public TextView pupil_name, pupil_age, tvReportFile, tvBillFile;
    public Button btnEdit, btnReport, btnBill, btnRemove;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public PupilViewHolder(@NonNull View itemView) {
        super( itemView );

        pupil_image = itemView.findViewById( R.id.pupil_image );
        pupil_name = itemView.findViewById( R.id.pupil_name );
        pupil_age = itemView.findViewById( R.id.pupil_age );
        tvReportFile = itemView.findViewById( R.id.report_file );
        tvBillFile = itemView.findViewById( R.id.bill_file );

        btnEdit = itemView.findViewById( R.id.btnEdit );
        btnReport = itemView.findViewById( R.id.btnReport );
        btnBill = itemView.findViewById( R.id.btnBill );
        btnRemove = itemView.findViewById( R.id.btnRemove );

        itemView.setOnCreateContextMenuListener(this);
        itemView.setOnClickListener( this );
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick( view, getAdapterPosition(), false );
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle( "Select the action" );

        contextMenu.add(0, 0, getAdapterPosition(), Common.VIEW_REPORT);
        contextMenu.add(0, 1, getAdapterPosition(), Common.VIEW_BILL);
        contextMenu.add(0, 2, getAdapterPosition(), Common.DELETE_REPORT);
        contextMenu.add(0, 2, getAdapterPosition(), Common.DELETE_BILL);
    }
}

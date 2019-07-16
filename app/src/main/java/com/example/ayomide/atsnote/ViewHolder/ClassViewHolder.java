package com.example.ayomide.atsnote.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

import com.example.ayomide.atsnote.Common.Common;
import com.example.ayomide.atsnote.Interface.ItemClickListener;
import com.example.ayomide.atsnote.R;

public class ClassViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
        View.OnCreateContextMenuListener
{

    public TextView tvClassName;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public ClassViewHolder(@NonNull View itemView) {
        super( itemView );

        tvClassName = itemView.findViewById( R.id.tvClass );

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

        contextMenu.add(0, 0, getAdapterPosition(), Common.EDIT);
        contextMenu.add(0, 1, getAdapterPosition(), Common.DELETE);
    }
}

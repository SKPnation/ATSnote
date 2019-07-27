package com.example.ayomide.atsnote.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ayomide.atsnote.R;

public class ParentViewHolder extends RecyclerView.ViewHolder {

    public TextView parent_name, parent_phone;
    public Button btn_edit, btn_delete;

    public ParentViewHolder(@NonNull View itemView) {
        super( itemView );

        parent_name = itemView.findViewById(R.id.parent_name);
        parent_phone = itemView.findViewById(R.id.parent_phone);
        btn_edit = itemView.findViewById( R.id.btnEdit );
        btn_delete = itemView.findViewById( R.id.btnRemove );
    }
}

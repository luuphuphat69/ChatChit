package com.example.chatchit;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class InsertLinkDialog extends DialogFragment {
    String link;
    @NonNull
    @Override
    public Dialog onCreateDialog( @Nullable Bundle savedInstanceState ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.insert_link_dialog, null))
                // Add action buttons
                .setPositiveButton("Thêm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.insert_link_dialog, (ViewGroup) getView(), false);
                        EditText input = viewInflated.findViewById(R.id.insertLink);
                        link = input.getText().toString();
                    }
                })
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }
    public String getInput(){
        return link;
    }
}

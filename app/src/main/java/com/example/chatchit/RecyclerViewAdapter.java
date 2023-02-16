package com.example.chatchit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolders> {
    private Context context;
    private ArrayList<Message> list;

    // Pass in parameters into the constructor
    public RecyclerViewAdapter(Context context, ArrayList<Message> list) {
        this.context = context;
        this.list = list;
    }
    // Adapter 3 primary methods
    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.message_layout, parent,false);

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolders holder, int position) {

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    // View holder defines below
    public class ViewHolders extends RecyclerView.ViewHolder {
        public ViewHolders(@NonNull View itemView) {
            super(itemView);
        }
    }
}

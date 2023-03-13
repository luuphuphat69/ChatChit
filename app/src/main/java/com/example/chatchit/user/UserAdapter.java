package com.example.chatchit.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatchit.IOnClickListener;
import com.example.chatchit.R;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> implements IOnClickListener {
    private ArrayList<User> Users;
    private IOnClickListener listener;
    public UserAdapter(ArrayList<User> users, IOnClickListener listener) {
        Users = users;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_row_layout,
                                                                     parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserViewHolder holder, int position) {
        User user = Users.get(position);
        holder.userName.setText(user.getUserName());
        holder.useremail.setText(user.getUserEmail());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickListener(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return Users.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView userName, useremail;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            useremail = itemView.findViewById(R.id.useremail);
        }
    }
    @Override
    public void onClickListener(User user) {
    }
}

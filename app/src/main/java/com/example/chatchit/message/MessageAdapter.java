package com.example.chatchit.message;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatchit.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolders> {
    private ArrayList<Message> Messages;
    private FirebaseAuth auth;
    private Context context;
    public MessageAdapter(ArrayList<Message> Messages, Context context){
        this.Messages = Messages;
        this.context = context;
    }
    @NonNull
    @Override
    public MessageViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.message_layout, parent, false);
        return new MessageViewHolders(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MessageViewHolders holder, int position) {
        Message message = Messages.get(position);
        auth = FirebaseAuth.getInstance();
        String currentUser = auth.getCurrentUser().getUid();

        holder.username.setText(Messages.get(position).getUserName());
        holder.message.setText(Messages.get(position).getUserMessage());
        holder.datetime.setText(Messages.get(position).getDatetime());
        if(message.getSenderId().equals(currentUser)){
            holder.messageRowLayout.setBackgroundColor(context.getResources().getColor(R.color.senderColor));
            holder.message.setTextColor(context.getResources().getColor(R.color.black));
        }else {
            holder.messageRowLayout.setBackgroundColor(context.getResources().getColor(R.color.receiverColor));
            holder.message.setTextColor(context.getResources().getColor(R.color.black));
        }
    }

    @Override
    public int getItemCount() {
        return Messages.size();
    }

    public class MessageViewHolders extends RecyclerView.ViewHolder {
        private TextView username, message, datetime;
        private LinearLayout messageRowLayout;
        public MessageViewHolders(@NonNull View itemView){
            super(itemView);
            username = itemView.findViewById(R.id.username);
            message = itemView.findViewById(R.id.usermessage);
            datetime = itemView.findViewById(R.id.message_datetime);
            messageRowLayout = itemView.findViewById(R.id.messageMain);
        }
    }
}

package com.example.chatchit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolders> {
    private Context context;
    private ArrayList<Message> Messages;

    public RecyclerViewAdapter(Context context, ArrayList<Message> Messages) {
        this.context = context;
        this.Messages = Messages;
    }
    // --Adapter 3 primary methods--
    /**
     * Khởi tạo views mới
     */
    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Nạp layout cho view biểu diễn phần tử message
        View view = LayoutInflater.from(context)
                .inflate(R.layout.message_layout, parent,false);
        return new ViewHolders(view);
    }
    /**
     *Thay thế contents của view
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolders holder, int position) {
        // Lấy phần tử từ dataset tại vị trí position
        Message message = Messages.get(position);
        //Thay thế contents của view bằng phần tử được lấy
        holder.username.setText(Messages.get(position).getUserEmail());
        holder.message.setText(Messages.get(position).getUserMessage());
        holder.datetime.setText(Messages.get(position).getDatetime());
    }

    /**
     * Trả về size của dataset
     */
    @Override
    public int getItemCount() {
        return Messages.size();
    }
    // --View holder defines below--
    public class ViewHolders extends RecyclerView.ViewHolder {
        private TextView username, message, datetime;
        public ViewHolders(@NonNull View itemView){
            super(itemView);
            username = itemView.findViewById(R.id.username);
            message = itemView.findViewById(R.id.usermessage);
            datetime = itemView.findViewById(R.id.message_datetime);
        }
    }
}

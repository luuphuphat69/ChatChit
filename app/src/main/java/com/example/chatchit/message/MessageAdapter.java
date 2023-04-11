package com.example.chatchit.message;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatchit.MyEditText;
import com.example.chatchit.R;
import com.example.chatchit.fragment.user.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolders> {
    private ArrayList<Message> Messages;
    private FirebaseAuth auth;
    private DatabaseReference db = FirebaseDatabase.getInstance("https://chatchit-81b07-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();;
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

        // Ẩn message có field isShown == 0
        if(message.getIsShown() == 0){
            holder.username.setVisibility(View.GONE);
            holder.message.setText("Tin nhắn đã bị xóa");
            holder.datetime.setVisibility(View.GONE);
            holder.userContentWebView.setVisibility(View.GONE);
        }
        // isShown == 1 (Mặc đinh)
        holder.username.setText(message.getUserName());
        holder.message.setText(message.getUserMessage());
        holder.datetime.setText(message.getDatetime());

        String data = message.getContentWebView();
        // Lấy url của kiểu MIME
        if(data != "null" && message.getReceiverId() == currentUser){
            // Load data vào WebView
            holder.userContentWebView.getSettings().setJavaScriptEnabled(true);
            holder.userContentWebView.setWebViewClient(new WebViewClient(){
                @Override
                public void onReceivedError( WebView view, int errorCode, String description, String failingUrl ) {
                    super.onReceivedError(view, errorCode, description, failingUrl);
                    Toast.makeText(view.getContext(), description, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onReceivedError( WebView view, WebResourceRequest request, WebResourceError error ) {
                    super.onReceivedError(view, request, error);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        onReceivedError(view, error.getErrorCode(), error.getDescription().toString(), request.getUrl().toString());
                    }
                }
            });
            holder.userContentWebView.loadUrl(data);
        }else{
            holder.userContentWebView.setVisibility(View.GONE);
        }

        holder.userContentWebView.setBackgroundColor(Color.TRANSPARENT);
        holder.userContentWebView.getSettings().setLoadWithOverviewMode(true);
        holder.userContentWebView.getSettings().setUseWideViewPort(true);

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
        private WebView userContentWebView;
        public MessageViewHolders(@NonNull View itemView){
            super(itemView);
            username = itemView.findViewById(R.id.username);
            message = itemView.findViewById(R.id.usermessage);
            datetime = itemView.findViewById(R.id.message_datetime);
            messageRowLayout = itemView.findViewById(R.id.messageMain);
            userContentWebView = itemView.findViewById(R.id.userContentWebView);
        }
    }
}

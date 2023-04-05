package com.example.chatchit.message;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;

import com.example.chatchit.MyEditText;
import com.example.chatchit.R;
import com.example.chatchit.login_signup.LoginSignup;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    MessageAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<Message> Messages;
    String contentWebview;
    FirebaseAuth ath;

    FirebaseUser user;
    DatabaseReference db;
    MyEditText inputMessage;
    FloatingActionButton send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        send = findViewById(R.id.send_icon);
        inputMessage = findViewById(R.id.inputMessage);
        recyclerView = findViewById(R.id.recyclerview);


        Messages = new ArrayList<Message>();
        db = FirebaseDatabase.getInstance("https://chatchit-81b07-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
        ath = FirebaseAuth.getInstance();
        user = ath.getCurrentUser();

        Intent intent = getIntent();
        String receiverId = intent.getStringExtra("receiverId");
        String senderId = ath.getCurrentUser().getUid();

        String uName = user.getDisplayName();
        String timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(Calendar.getInstance().getTime());

        /*
        * Sự kiện gửi tin nhắn
        * */
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = inputMessage.getText().toString();
                contentWebview = MyEditText.getLink();
                // Lưu tin nhắn trong firebase, child là "Messages"
                // và reset lại phần nhập tin nhắn
                db.child("Messages").push().setValue(new Message(uName, msg, timeStamp, contentWebview, senderId, receiverId, 1)).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        inputMessage.setText("");
                        // Set lại link URL của contentWebView
                        // Trách trường hợp khi nhắn tới user khác sẽ hiện
                        // lại Item đã gửi đến user trước đó.
                        MyEditText.setLink();
                    }
                });
            }
        });
        adapter = new MessageAdapter(Messages, ChatActivity.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return  true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        receiveMessages();
    }
    /*
    * Lấy dữ liệu từ máy chủ và cập nhật các mục nhận được khi người dùng
    * cuộn danh sách.
    * Cập nhật tin nhắn trong recyclerview.
    * */
    private void receiveMessages(){
        Intent intent = getIntent();
        String senderId = ath.getCurrentUser().getUid();
        String receiverId = intent.getStringExtra("receiverId");

        db.child("Messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Messages.clear();
                // Đi qua từng child trong firebase
                // để lấy dữ liệu
                for(DataSnapshot snap:snapshot.getChildren()){
                    Message message = snap.getValue(Message.class);
                    // Chỉ hiện thi tin nhắn giữa 2 người.
                    // Nếu không có điều kiện thì mọi người sẽ thấy
                    // tất cả tin nhắn của nhau.
                    if((senderId.equals(message.getSenderId()) && receiverId.equals(message.getReceiverId())) ||
                        (senderId.equals(message.getReceiverId()) && receiverId.equals(message.getSenderId()))) {
                        if(message.getIsShown() != 0){
                            Messages.add(message);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    //Menu logout
    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.delete_menu, menu);
        MenuBuilder menuBuilder = (MenuBuilder) menu;
        menuBuilder.setOptionalIconsVisible(true);
        return true;
    }
    // Xóa đoạn chat
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = getIntent();
        String senderId = ath.getCurrentUser().getUid();
        String receiverId = intent.getStringExtra("receiverId");
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.deleteConvo:
                db.child("Messages").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Đi qua từng child trong firebase
                        // để lấy dữ liệu.
                        for(DataSnapshot snap:snapshot.getChildren()){
                            Message message = snap.getValue(Message.class);
                            String childKey = snap.getKey();
                            // Điều kiện để lấy data giữa 2 người
                            if((senderId.equals(message.getSenderId()) && receiverId.equals(message.getReceiverId())) ||
                                    (senderId.equals(message.getReceiverId()) && receiverId.equals(message.getSenderId()))) {
                                if(message.getIsShown() == 0 || message.getIsShown() == 1){
                                    db.child("Messages").child(childKey).removeValue();
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG, error.getMessage());
                    }
                });
                finish();
            case R.id.delete:
                db.child("Messages").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Đi qua từng child trong firebase
                        // để lấy dữ liệu.
                        for(DataSnapshot snap:snapshot.getChildren()){
                            Message message = snap.getValue(Message.class);
                            // Điều kiện để lấy data giữa 2 người
                            if((senderId.equals(message.getSenderId()) && receiverId.equals(message.getReceiverId())) ||
                                    (senderId.equals(message.getReceiverId()) && receiverId.equals(message.getSenderId()))) {
                                if(senderId.equals(message.getSenderId())){
                                    message.setIsShown(0);
                                    if(message.getIsShown() == 0){
                                        // Update giá trị isShown của từng message thành 0
                                        // 0: Hide
                                        // 1: Shown
                                        HashMap<String, Object> taskMap = new HashMap<String, Object>();
                                        taskMap.put("isShown", 0);
                                        snap.getRef().updateChildren(taskMap);
                                        finish();
                                    }
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG, error.getMessage());
                    }
                });
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
package com.example.chatchit.message;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

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

public class ChatActivity extends AppCompatActivity {

    MessageAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<Message> Messages;
    FirebaseAuth ath;
    FirebaseUser user;
    DatabaseReference db;
    TextInputLayout inputMessage;
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
        * S??? ki???n g???i tin nh???n
        * */
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = inputMessage.getEditText().getText().toString();
                // L??u tin nh???n trong firebase, child l?? "Messages"
                // v?? reset l???i ph???n nh???p tin nh???n
                db.child("Messages").push().setValue(new Message(uName, msg, timeStamp, senderId, receiverId)).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        inputMessage.getEditText().setText("");
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
    * L???y d??? li???u t??? m??y ch??? v?? c???p nh???t c??c m???c nh???n ???????c khi ng?????i d??ng
    * cu???n danh s??ch.
    * C???p nh???t tin nh???n trong recyclerview.
    * */
    private void receiveMessages(){
        Intent intent = getIntent();
        String senderId = ath.getCurrentUser().getUid();
        String receiverId = intent.getStringExtra("receiverId");

        db.child("Messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Messages.clear();
                // ??i qua t???ng child trong firebase
                // ????? l???y d??? li???u
                for(DataSnapshot snap:snapshot.getChildren()){
                    Message message = snap.getValue(Message.class);
                    // Ch??? hi???n thi tin nh???n gi???a 2 ng?????i.
                    // N???u kh??ng c?? ??i???u ki???n th?? m???i ng?????i s??? th???y
                    // t???t c??? tin nh???n c???a nhau.
                    if((senderId.equals(message.getSenderId()) && receiverId.equals(message.getReceiverId())) ||
                        (senderId.equals(message.getReceiverId()) && receiverId.equals(message.getSenderId()))) {
                        Messages.add(message);
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.btn_logout:
                ath.signOut();
                startActivity(new Intent(this, LoginSignup.class));
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
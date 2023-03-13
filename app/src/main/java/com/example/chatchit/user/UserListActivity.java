package com.example.chatchit.user;

import android.accounts.Account;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatchit.IOnClickListener;
import com.example.chatchit.accountsetting.AccountSettingActivity;
import com.example.chatchit.login_signup.LoginSignup;
import com.example.chatchit.message.MainActivity;
import com.example.chatchit.R;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserListActivity extends AppCompatActivity implements IOnClickListener {
    ArrayList<User> Users;
    UserAdapter userAdapter;
    RecyclerView userrecyclerView;
    FirebaseAuth ath;
    String currentUser;
    BottomNavigationView bottomNavigationView;
    private DatabaseReference database;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        // Biến tham chiếu tới db
        database = FirebaseDatabase.getInstance("https://chatchit-81b07-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
        ath = FirebaseAuth.getInstance();
        userrecyclerView = findViewById(R.id.userRecyclerView);
        Users = new ArrayList<User>();
        currentUser = ath.getCurrentUser().getUid();

        bottomNavigationView = findViewById(R.id.bottomNav_message);

        userAdapter = new UserAdapter(Users, new IOnClickListener() {
            @Override
            public void onClickListener(User user) {
                Intent intent = new Intent(UserListActivity.this, MainActivity.class);
                intent.putExtra("receiverId", user.getUserId());
                startActivity(intent);
            }
        });

        userrecyclerView.setAdapter(userAdapter);
        userrecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        /*
        * Cập nhật danh sách người dùng ở recyclerview
        * */
        database.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Đi qua từng lớp của child "User" để lấy dữ liệu.
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                        String uid = dataSnapshot.getKey();
                        // Chỉ hiện thị người dùng ngoại trừ tài khoản
                        // đang login.
                        if(!uid.equals(currentUser)){
                            User user = dataSnapshot1.getValue(User.class);
                            Users.add(user);
                        }
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        /*
        * Thanh bottom navigate, chuyển người dùng đến trang khác
        * */
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.userList:
                        return true;
                    case R.id.account:
                        Intent intent = new Intent(UserListActivity.this, AccountSettingActivity.class);
                        startActivity(intent);
                        return true;
                }
                return  false;
            }
        });
    }
    //Menu của bottom navigate
    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        MenuBuilder menuBuilder = (MenuBuilder) menu;
        menuBuilder.setOptionalIconsVisible(true);
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
    @Override
    public void onClickListener(User user) {
    }
}

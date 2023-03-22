package com.example.chatchit.fragment.user;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatchit.IOnClickListener;
import com.example.chatchit.R;
import com.example.chatchit.message.ChatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserFragment extends Fragment implements IOnClickListener {
    ArrayList<User> Users;
    UserAdapter userAdapter;
    RecyclerView userrecyclerView;
    FirebaseAuth ath;
    String currentUser;
    private DatabaseReference database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Biến tham chiếu tới db
        database = FirebaseDatabase.getInstance("https://chatchit-81b07-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
        ath = FirebaseAuth.getInstance();
        userrecyclerView = view.findViewById(R.id.userRecyclerView);
        Users = new ArrayList<User>();
        currentUser = ath.getCurrentUser().getUid();

        userAdapter = new UserAdapter(getContext(), Users, new IOnClickListener() {
            @Override
            public void onClickListener(User user) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("receiverId", user.getUserId());

                startActivity(intent);
            }
        });

        userrecyclerView.setAdapter(userAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        userrecyclerView.addItemDecoration(dividerItemDecoration);
        userrecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

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

    }
    @Override
    public void onClickListener(User user) {
    }
}
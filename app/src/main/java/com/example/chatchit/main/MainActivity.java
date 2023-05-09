package com.example.chatchit.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.chatchit.fragment.account.MyAccountFragment;
import com.example.chatchit.fragment.setting.SettingPreferenceFragment;
import com.example.chatchit.fragment.social.SocialFragment;
import com.example.chatchit.fragment.user.User;
import com.example.chatchit.fragment.user.UserAdapter;
import com.example.chatchit.fragment.user.UserFragment;
import com.example.chatchit.login_signup.LoginSignup;
import com.example.chatchit.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    ActionBar toolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNav_main);

        toolbar = getSupportActionBar();
        toolbar.setTitle("Tin nhắn");

        // load UserFragment mặc định
        loadFragment(new UserFragment());
        /*
        * Thanh bottom navigate, chuyển người dùng đến fragment khác
        * */
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()){
                    case R.id.userList:
                        toolbar.setTitle("Tin nhắn");
                        fragment = new UserFragment();
                        loadFragment(fragment);
                        return  true;
                    case R.id.account:
                        toolbar.setTitle("Tài khoản");
                        fragment = new MyAccountFragment();
                        loadFragment(fragment);
                        return  true;
                    case R.id.setting:
                        toolbar.setTitle("Cài đặt");
                        fragment = new SettingPreferenceFragment();
                        loadFragment(fragment);
                        return true;
                    case R.id.social:
                        toolbar.setTitle("MXH");
                        fragment = new SocialFragment();
                        loadFragment(fragment);
                        return true;
                }
                return  false;
            }
        });
    }
    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    //Menu sign out
    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);
        MenuBuilder menuBuilder = (MenuBuilder) menu;
        menuBuilder.setOptionalIconsVisible(true);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.btn_logout:
                auth.signOut();
                startActivity(new Intent(this, LoginSignup.class));
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
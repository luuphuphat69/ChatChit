package com.example.chatchit.accountsetting;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.chatchit.R;
import com.example.chatchit.fragment.AccountFragmentAdapter;
import com.example.chatchit.user.UserListActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class AccountSettingActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    AccountFragmentAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accountsetting_layout);
        /*
        * Bottom navigation
        * */
        bottomNavigationView = findViewById(R.id.bottomNav_account);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = null;
                switch (item.getItemId())
                {
                    case R.id.userList:
                        if(intent == null) {
                            intent = new Intent(AccountSettingActivity.this, UserListActivity.class);
                            startActivity(intent);
                        }
                        return  true;
                    case R.id.account:
                        return true;
                }
                return  false;
            }
        });
        /*
        * Tab layout
        * */
        tabLayout = findViewById(R.id.myTab);
        viewPager2 = findViewById(R.id.pager);

        adapter = new AccountFragmentAdapter(AccountSettingActivity.this);
        viewPager2.setAdapter(adapter);

        // Set title cho các tab
        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position){
                    case 0:
                        tab.setText("Tài khoản của tôi");
                        break;
                    case 1:
                        tab.setText("Danh sách yêu thích");
                        break;
                }
            }
        }).attach();
    }
}

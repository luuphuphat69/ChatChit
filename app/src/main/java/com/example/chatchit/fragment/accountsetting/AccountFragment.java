package com.example.chatchit.fragment.accountsetting;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatchit.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class AccountFragment extends Fragment {
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    AccountFragmentAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /*
         * Tab layout
         * */
        tabLayout = view.findViewById(R.id.myTab);
        viewPager2 = view.findViewById(R.id.pager);

        adapter = new AccountFragmentAdapter(getActivity());
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
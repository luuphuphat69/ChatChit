package com.example.chatchit.fragment.accountsetting;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class AccountFragmentAdapter extends FragmentStateAdapter {

    public AccountFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new WishlistFragment();
            default:
                return new MyAccountFragment();
        }
    }
    @Override
    public int getItemCount() {
        return 2;
    }
}
package com.example.chatchit.fragment.setting;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.chatchit.R;
import com.example.chatchit.message.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SettingPreferenceFragment extends PreferenceFragmentCompat{
    DatabaseReference db = FirebaseDatabase.getInstance("https://chatchit-81b07-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    boolean nightMODE;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    public void onCreatePreferences( @Nullable Bundle savedInstanceState, @Nullable String rootKey ) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        Preference backUp = getPreferenceManager().findPreference("backup");
        Preference restore = getPreferenceManager().findPreference("restore");
        Preference switcher = getPreferenceManager().findPreference("switch");

        sharedPreferences = getContext().getSharedPreferences("MODE", Context.MODE_PRIVATE);
        nightMODE = sharedPreferences.getBoolean("night", false);

        // Bật tắt theme app
        switcher.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                if (nightMODE == true){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("night",false);
                }
                else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("night", true);
                }
                editor.apply();
                return false;
            }
        });

        backUp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                return false;
            }
        });
        // Hiện thị đoạn chat bị ẩn
        restore.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                checkMessage();
                Toast.makeText(getContext(),"Restore successfull", Toast.LENGTH_SHORT).show();
                return  true;
            }
        });
    }
    public void checkMessage(){
        String senderId = auth.getCurrentUser().getUid();
        db.child("Messages").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange( @NonNull DataSnapshot snapshot ) {
                for(DataSnapshot snap:snapshot.getChildren()){
                   Message message = snap.getValue(Message.class);
                    if(senderId.equals(message.getSenderId())){
                        if(message.getIsShown() == 0){
                            // Update giá trị isShown của từng message thành 0
                            // 0: Hide
                            // 1: Shown
                            HashMap<String, Object> taskMap = new HashMap<String, Object>();
                            taskMap.put("isShown", 1);
                            snap.getRef().updateChildren(taskMap);
                        }
                    }
                }
            }
            @Override
            public void onCancelled( @NonNull DatabaseError error ) {
                Log.d(getTag(), error.getMessage());
            }
        });
    }
}
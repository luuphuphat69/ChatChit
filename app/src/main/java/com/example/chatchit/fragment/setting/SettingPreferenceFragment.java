package com.example.chatchit.fragment.setting;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;

public class SettingPreferenceFragment extends PreferenceFragmentCompat{
    DatabaseReference db = FirebaseDatabase.getInstance("https://chatchit-81b07-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String backUpMess;
    boolean nightMODE;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    public void onCreatePreferences( @Nullable Bundle savedInstanceState, @Nullable String rootKey ) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        Preference backUp = getPreferenceManager().findPreference("backup");
        Preference restore = getPreferenceManager().findPreference("restore");
        Preference switcher = getPreferenceManager().findPreference("switch");
        Preference button = getPreferenceManager().findPreference("button");

        sharedPreferences = getContext().getSharedPreferences("MODE", Context.MODE_PRIVATE);
        nightMODE = sharedPreferences.getBoolean("night", false);

        // Bật tắt theme app
        switcher.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                if (nightMODE){
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
        // Lưu trữ dữ liệu vào trong máy bằng Internal Storage
        backUp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick( @NonNull Preference preference ) {
                String senderId = auth.getCurrentUser().getUid();
                db.child("Messages").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange( @NonNull DataSnapshot snapshot ) {
                        for(DataSnapshot snap:snapshot.getChildren()){
                            Message message = snap.getValue(Message.class);
                            if(senderId.equals(message.getSenderId()) || senderId.equals(message.getReceiverId())){
                                if(message.getIsShown() == 0 || message.getIsShown() == 1){
                                    backUpMess +=   " message: " + message.getUserMessage() + " content: " + message.getContentWebView()
                                                  + " sender: " + senderId + " receiver: " + message.getReceiverId()
                                                  + " time: " + message.getDatetime() + "\n";
                                    writeFileOnInternalStorage(getContext(), "backup", backUpMess);
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled( @NonNull DatabaseError error ) {
                        Log.d(getTag(), error.getMessage());
                    }
                });
                Toast.makeText(getContext(), "Back up success", Toast.LENGTH_LONG).show();
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
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick( @NonNull Preference preference ) {
                Intent intent = new Intent(getActivity(), TeamsActivity.class);
                startActivity(intent);
                return true;
            }
        });
    }
    /* Kiểm tra từng đoạn chat, nếu senderId của Message = current user ID
    *  và giá trị isShown = 0 thì set lại isShown = 1
    */
    public void checkMessage(){
        String senderId = auth.getCurrentUser().getUid();
        db.child("Messages").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange( @NonNull DataSnapshot snapshot ) {
                for(DataSnapshot snap:snapshot.getChildren()){
                   Message message = snap.getValue(Message.class);
                    if(senderId.equals(message.getSenderId())){
                        if(message.getIsShown() == 0){
                            // Update giá trị isShown của từng message thành 1
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
    // Đẩy file vào internal storage
    public void writeFileOnInternalStorage(Context mcoContext, String sFileName, String sBody){
        // Tham chiếu đến file directory  /data/data/Project_PackageName/files/messagesbackup
        File dir = new File(mcoContext.getFilesDir(), "messagesbackup");
        // Nếu chưa tồn tại thì tạo file
        if(!dir.exists()){
            dir.mkdir();
        }
        try {
            File file = new File(dir, sFileName);
            FileWriter writer = new FileWriter(file);
            // Thêm nội dung vào cuối file
            writer.append(sBody);
            // Xoá hết dữ liệu trong buffer của stream writter trước khi load data mới vào
            writer.flush();
            //Đóng stream writer
            writer.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
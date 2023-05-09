package com.example.chatchit.fragment.account;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.chatchit.R;
import com.example.chatchit.fragment.user.User;
import com.example.chatchit.fragment.user.UserAdapter;
import com.example.chatchit.login_signup.SignUpActivity;
import com.example.chatchit.main.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditInfoActivity extends AppCompatActivity {
    TextInputEditText editUsername, useremail;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference db = FirebaseDatabase.getInstance("https://chatchit-81b07-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
    CircleImageView userImg;
    Button saveChanges;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);
        setTitle("Thay đổi thông tin");

        editUsername = findViewById(R.id.editUsername);
        useremail = findViewById(R.id.useremail);
        userImg = findViewById(R.id.userImage);
        saveChanges = findViewById(R.id.saveChanges);


        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String email = intent.getStringExtra("email");

        editUsername.setText(name);
        useremail.setText(email);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference islandRef = storageReference.child("Profile Images/").child(auth.getCurrentUser().getPhotoUrl().toString());
        islandRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess( Uri uri ) {
                String url = uri.toString();
                Glide.with(EditInfoActivity.this)
                        .load(url)
                        .into(userImg);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure( @NonNull Exception e ) {
                Toast.makeText(EditInfoActivity.this , e.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("userImg", e.getMessage());
            }
        });

        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editUsername.getText().toString();
                Intent intentCallBack = getIntent();
                intentCallBack.putExtra("name", username);
                changeNameInDb(username);

                setResult(RESULT_OK, intentCallBack);
                finish();
            }
        });
    }
    // Thay đổi user name trong database
    public void changeNameInDb(String displayName){
        String currentUser = auth.getCurrentUser().getUid();
        db.child("User").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange( @NonNull DataSnapshot snapshot ) {
                for(DataSnapshot snap: snapshot.getChildren()){
                    for(DataSnapshot dataSnapshot1: snap.getChildren()){
                        String uid = snap.getKey();
                        // Chỉ hiện thị tài khoản login
                        if(uid.equals(currentUser)){
                            HashMap<String, Object> taskMap = new HashMap<String, Object>();
                            taskMap.put("userName", displayName);
                            dataSnapshot1.getRef().updateChildren(taskMap);
                        }
                    }
                }
            }
            @Override
            public void onCancelled( @NonNull DatabaseError error ) {
            }
        });
    }
}

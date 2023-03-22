package com.example.chatchit.fragment.account;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatchit.R;
import com.example.chatchit.fragment.user.User;
import com.example.chatchit.fragment.user.UserAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyAccountFragment extends Fragment {

    ImageView editUsername;
    CircleImageView userImage;
    TextView username, email;
    DatabaseReference database;
    FirebaseAuth auth;
    User user;

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == Activity.RESULT_OK){
                // Set lại display name của user
                String UserName = result.getData().getStringExtra("name");
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(UserName).build();

                FirebaseUser firebaseUser = auth.getCurrentUser();
                firebaseUser.updateProfile(profileUpdates);
                username.setText(UserName);
            }
        }
    });
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = FirebaseDatabase.getInstance("https://chatchit-81b07-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
        auth = FirebaseAuth.getInstance();

        username = view.findViewById(R.id.username);
        email = view.findViewById(R.id.email);

        username.setText(auth.getCurrentUser().getDisplayName());
        email.setText(auth.getCurrentUser().getEmail());

        userImage = view.findViewById(R.id.userImage);
        editUsername = view.findViewById(R.id.editUsername);

        /*getUserInfo();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference islandRef = storageReference.child("Profile Images/").child(user.getUserImg());
        try {
            File localFile = File.createTempFile("images", "jpg");

            islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getPath());
                    userImage.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(getContext(), exception.getMessage() + user.getUserImg(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }*/


        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditInfoActivity.class);
                intent.putExtra("name", username.getText().toString());
                intent.putExtra("email", email.getText().toString());

                launcher.launch(intent);
            }
        });

        editUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditInfoActivity.class);
                intent.putExtra("name", username.getText().toString());
                intent.putExtra("email", email.getText().toString());

                launcher.launch(intent);
            }
        });
    }
    public void getUserInfo(){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        db.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( @NonNull DataSnapshot snapshot ) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                        String uid = dataSnapshot.getKey();
                        // Chỉ hiện thị người dùng ngoại trừ tài khoản
                        // đang login.
                        if(uid.equals(auth.getCurrentUser().getUid())){
                            user = dataSnapshot1.getValue(User.class);
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
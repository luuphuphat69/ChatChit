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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyAccountFragment extends Fragment {

    ImageView editUsername;
    CircleImageView userImage;
    TextView username, email;
    DatabaseReference database = FirebaseDatabase.getInstance("https://chatchit-81b07-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
    FirebaseAuth auth = FirebaseAuth.getInstance();

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

        username = view.findViewById(R.id.username);
        email = view.findViewById(R.id.email);

        username.setText(auth.getCurrentUser().getDisplayName());
        email.setText(auth.getCurrentUser().getEmail());

        userImage = view.findViewById(R.id.userImage);
        editUsername = view.findViewById(R.id.editUsername);

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

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference islandRef = storageReference.child("Profile Images/").child(auth.getCurrentUser().getPhotoUrl().toString());

        islandRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess( Uri uri ) {
                String url = uri.toString();
                Glide.with(getContext())
                        .load(url)
                        .into(userImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure( @NonNull Exception e ) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("userImg", e.getMessage());
            }
        });
    }
}
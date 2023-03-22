package com.example.chatchit.login_signup;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import com.example.chatchit.R;
import com.example.chatchit.fragment.user.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {
    TextInputLayout signUpEmail, signUpPassword, signUpUsername;
    Button signup;
    CircleImageView addUserImg;
    FirebaseAuth auth;
    Uri uri;
    String randomUUID;
    DatabaseReference db;

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult( ActivityResult result ) {
            if(result.getResultCode() == RESULT_OK){
                Intent data = result.getData();
                uri = data.getData();

                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                randomUUID = UUID.randomUUID().toString();
                StorageReference ref = storageRef.child("Profile Images").child(randomUUID);
                ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess( UploadTask.TaskSnapshot taskSnapshot ) {
                        Toast.makeText(SignUpActivity.this, "Update image successful", Toast.LENGTH_LONG).show();
                    }
                }). addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure( @NonNull Exception e ) {
                        Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                addUserImg.setImageURI(uri);
            }
        }
    });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_layout);

        signUpEmail = findViewById(R.id.signUpEmail);
        signUpPassword = findViewById(R.id.signUpPassword);
        signUpUsername = findViewById(R.id.signUpUsername);
        addUserImg = findViewById(R.id.addUserImg);
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance("https://chatchit-81b07-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();

        addUserImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                Intent photoPicker = new Intent(Intent.ACTION_GET_CONTENT);
                photoPicker.setType("image/*");
                launcher.launch(photoPicker);
            }
        });

        signup = findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = signUpEmail.getEditText().getText().toString();
                String Password = signUpPassword.getEditText().getText().toString();
                String UserName = signUpUsername.getEditText().getText().toString();

                auth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            db.child("User").child(auth.getUid()).push().setValue(new User(auth.getUid(), UserName, Email, Password, randomUUID));

                            // Set DisplayName cho user đăng ký
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(UserName).build();
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            firebaseUser.updateProfile(profileUpdates);

                            Toast.makeText(SignUpActivity.this, "Tạo tài khoản thành công", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(SignUpActivity.this, LoginSignup.class));
                        }else{
                            Toast.makeText(SignUpActivity.this, "Tạo tài khoản không thành công", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return  true;
    }
}
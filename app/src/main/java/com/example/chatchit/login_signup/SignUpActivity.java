package com.example.chatchit.login_signup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatchit.R;
import com.example.chatchit.user.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    TextInputLayout signUpEmail, signUpPassword, signUpUsername;
    Button signup;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference db;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_layout);

        signUpEmail = findViewById(R.id.signUpEmail);
        signUpPassword = findViewById(R.id.signUpPassword);
        signUpUsername = findViewById(R.id.signUpUsername);

        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance("https://chatchit-81b07-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();

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
                            db.child("User").child(auth.getUid()).push().setValue(new User(auth.getUid(), UserName, Email, Password));

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

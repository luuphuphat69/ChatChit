package com.example.chatchit.login_signup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.chatchit.R;
import com.example.chatchit.main.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginSignup extends AppCompatActivity {

    TextInputLayout email, password;
    Button mainSignup, login;
    FirebaseAuth auth;
    DatabaseReference db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_signup);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        mainSignup = findViewById(R.id.mainSignup);
        login = findViewById(R.id.login);
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance("https://chatchit-81b07-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
        /*
        * Sự kiện đăng ký
         */
        mainSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginSignup.this, SignUpActivity.class));
            }
        });
        /*
        * Sự kiện đăng nhập
        * */
        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void  onClick(View v){
                String Email = email.getEditText().getText().toString();
                String Password = password.getEditText().getText().toString();
                auth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            startActivity(new Intent(LoginSignup.this, MainActivity.class));
                        } else{
                            Toast.makeText(LoginSignup.this, "Đăng nhập thất bại", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
    /*
    * Kiểm tra user đã đăng nhập hay chưa ?
    * Nếu có thì chuyển sang UserListActivity
    * */
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = auth.getCurrentUser();
        if(user != null){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
}
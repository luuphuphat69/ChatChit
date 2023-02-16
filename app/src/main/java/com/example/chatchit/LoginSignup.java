package com.example.chatchit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginSignup extends AppCompatActivity {

    TextInputLayout email, password;
    Button signup, login;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_signup);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        signup = findViewById(R.id.signup);
        login = findViewById(R.id.login);

        auth = FirebaseAuth.getInstance();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = email.getEditText().getText().toString();
                String Password = password.getEditText().getText().toString();
                auth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(LoginSignup.this, "Tạo tài khoản thành công", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(LoginSignup.this, "Tạo tài khoản không thành công", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void  onClick(View v){
                String Email = email.getEditText().getText().toString();
                String Password = password.getEditText().getText().toString();
                auth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            startActivity(new Intent(LoginSignup.this, MainActivity.class));
                        }else{
                            Toast.makeText(LoginSignup.this, "Đăng nhập thất bại", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
}
package com.example.fixokrop;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;

public class User extends Activity {
    EditText username,password;
    TextView reg;
    TextView login;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        username=findViewById(R.id.etname);
        password=findViewById(R.id.etpassword);
        login=findViewById(R.id.btnlogin);
        FirebaseApp.initializeApp(this);
        reg = findViewById(R.id.register);

        firebaseAuth=FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username.getText().toString().equals("")||password.getText().toString().equals("")) {
                    Toast.makeText(User.this, "Please fill in Details", Toast.LENGTH_SHORT).show();
                    return;
                }
                validate(username.getText().toString(),password.getText().toString());
            }
        });

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(User.this,Registration.class);
                startActivity(it);
            }
        });


    }
    private void validate(String username,String userpassword) {

        firebaseAuth.signInWithEmailAndPassword(username, userpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("isFirstRun", false).apply();
                    Toast.makeText(User.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(User.this, MainActivity.class));
                } else {
                    Toast.makeText(User.this, "Login Failed", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }
}



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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;


public class Registration extends Activity {
    private EditText userpassword,useremail;
    TextView rebtn;
    TextView sign;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        useremail=findViewById(R.id.etuseremail);
        userpassword=findViewById(R.id.etuserpassword);
        rebtn=findViewById(R.id.btnregister);
        firebaseAuth=FirebaseAuth.getInstance();
        sign = findViewById(R.id.sign);

        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Registration.this,User.class);
                startActivity(it);
            }
        });

        rebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    String user_email= useremail.getText().toString().trim();
                    String user_password=userpassword.getText().toString().trim();
                    firebaseAuth.createUserWithEmailAndPassword(user_email,user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Registration.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Registration.this,User.class));
                            }else {
                                Toast.makeText(Registration.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }
    private Boolean validate(){
        Boolean result=false;
        String email = useremail.getText().toString();
        String passsword = userpassword.getText().toString();

        if(email.isEmpty() || passsword.isEmpty()){
            Toast.makeText(this, "Please Enter the all Details", Toast.LENGTH_SHORT).show();
        } else{
            result = true;

        } return result;

    }

}
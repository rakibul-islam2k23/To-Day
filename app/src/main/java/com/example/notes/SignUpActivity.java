package com.example.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {
    ImageButton back;
    EditText emailEditText,passwordEditText,confirmEditText;
    TextView signUp;
    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        back = findViewById(R.id.back);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmEditText = findViewById(R.id.confirmPasswordEditText);
        signUp = findViewById(R.id.signUp);
        progressBar = findViewById(R.id.progressBar);

        firebaseAuth = FirebaseAuth.getInstance();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String confirmPassword = confirmEditText.getText().toString().trim();

                if(email.isEmpty()){
                    emailEditText.setError("Please enter email");
                    emailEditText.requestFocus();
                }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    emailEditText.setError("Invalid Email");
                    emailEditText.requestFocus();
                }else if(password.isEmpty()){
                    passwordEditText.setError("Please enter password");
                    passwordEditText.requestFocus();
                }else if(password.length() < 6){
                    passwordEditText.setError("Password must be at least 6 characters");
                    passwordEditText.requestFocus();
                }else if (confirmPassword.isEmpty()){
                    confirmEditText.setError("Please enter password");
                    confirmEditText.requestFocus();
                }else if(confirmPassword.length() < 6){
                    confirmEditText.setError("Password must be at least 6 characters");
                    confirmEditText.requestFocus();
                }else  if(!password.equals(confirmPassword)){
                    confirmEditText.setError("Password not matched");
                }
                else{
                    signUp.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    firebaseAuth.createUserWithEmailAndPassword(email,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);
                                signUp.setVisibility(View.VISIBLE);
                                SentEmailVerification();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressBar.setVisibility(View.GONE);
                                    signUp.setVisibility(View.VISIBLE);
                                    Toast.makeText(SignUpActivity.this, ""+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }


            }
        });
    }

    private void SentEmailVerification() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressBar.setVisibility(View.GONE);
                    signUp.setVisibility(View.VISIBLE);
                    Toast.makeText(SignUpActivity.this, "Email sent for verification !", Toast.LENGTH_SHORT).show();
                    firebaseAuth.signOut();
                    finish();
                    Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
                    startActivity(intent);
                }
            });
        }else{
            progressBar.setVisibility(View.GONE);
            signUp.setVisibility(View.VISIBLE);
            Toast.makeText(SignUpActivity.this, "Email sent failed !", Toast.LENGTH_SHORT).show();
        }

    }
}
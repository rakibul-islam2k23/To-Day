package com.example.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateNoteActivity extends AppCompatActivity {
    ImageButton back, save;
    EditText title, content;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    String tit, con;
    RelativeLayout colorLayout;
    String stringTitle, stringContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        back = findViewById(R.id.back);
        save = findViewById(R.id.save);
        title = findViewById(R.id.title);
        content = findViewById(R.id.content);
        colorLayout = findViewById(R.id.colorLayout);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("notes")
                .child(firebaseUser.getUid()).child("myNotes");


        Intent intent = getIntent();
        stringTitle = intent.getStringExtra("title");
        stringContent = intent.getStringExtra("content");
        int check = intent.getIntExtra("int", 0);

        title.setText(stringTitle);
        content.setText(stringContent);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tit = title.getText().toString();
                con = content.getText().toString();
                if (tit.isEmpty()) {
                    title.setError("Please enter title");
                    title.requestFocus();
                }else if(con.isEmpty()){
                    content.setError("please enter content");
                    content.requestFocus();
                }else{
                    StoreInFirebase();
                }

            }
        });

    }

    private void StoreInFirebase() {
        FireBaseModelClass fireBaseModelClass = new FireBaseModelClass(tit, con);
        databaseReference.child(tit).setValue(fireBaseModelClass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(CreateNoteActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreateNoteActivity.this, "" + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}


//
//    DocumentReference documentReference = firebaseFirestore.collection("notes")
//            .document(firebaseUser.getUid())
//            .collection("myNotes")
//            .document();
//    Map<String,Object> map = new HashMap();
//        map.put("title",tit);
//                map.put("content",con);
//                documentReference.set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
//@Override
//public void onComplete(@NonNull Task<Void> task) {
//        Toast.makeText(CreateNoteActivity.this, "Saved", Toast.LENGTH_SHORT).show();
//        finish();
//        }
//        }).addOnFailureListener(new OnFailureListener() {
//@Override
//public void onFailure(@NonNull Exception e) {
//        Toast.makeText(CreateNoteActivity.this, "Failed", Toast.LENGTH_SHORT).show();
//        }
//        });
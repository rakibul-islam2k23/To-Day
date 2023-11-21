package com.example.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class HomeActivity extends AppCompatActivity {
    TextView create;
    RecyclerView recyclerView;
    ArrayList<FireBaseModelClass> arrayList = new ArrayList<>();
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    FireBaseAdapter fireBaseAdapter;
    SearchView searchView;
    ProgressBar progressBarOfHome;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        create = findViewById(R.id.create);
        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.searchView);
        progressBarOfHome = findViewById(R.id.progressBarOfHome);
        toolbar = findViewById(R.id.toolBar);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        setSupportActionBar(toolbar);

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setHasFixedSize(true);
        fireBaseAdapter = new FireBaseAdapter(HomeActivity.this,arrayList);



        GetDataFromFirebase();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                GetSearch(newText);
                return false;
            }
        });




        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,CreateNoteActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(HomeActivity.this).inflate(R.menu.toolbar_menu_layout,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.logOut) {
            firebaseAuth.signOut();
            Intent intent = new Intent(HomeActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void GetSearch(String newText) {
        Query query = FirebaseDatabase.getInstance()
                .getReference("notes")
                .child(firebaseUser.getUid())
                .child("myNotes")
                .orderByChild("title")
                .startAt(newText)
                .endAt(newText+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    FireBaseModelClass fireBaseModelClass = snapshot1.getValue(FireBaseModelClass.class);
                    arrayList.add(fireBaseModelClass);
                }

                recyclerView.setAdapter(fireBaseAdapter);
                fireBaseAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Searching error", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void GetDataFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("notes")
                .child(firebaseUser.getUid()).child("myNotes");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBarOfHome.setVisibility(View.GONE);
                arrayList.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    FireBaseModelClass fireBaseModelClass = snapshot1.getValue(FireBaseModelClass.class);
                    arrayList.add(fireBaseModelClass);
                }

                recyclerView.setAdapter(fireBaseAdapter);
                fireBaseAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Fatch Error", Toast.LENGTH_SHORT).show();
            }
        });

    }


}
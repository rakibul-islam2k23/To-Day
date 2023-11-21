package com.example.notes;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FireBaseAdapter extends RecyclerView.Adapter<FireBaseAdapter.ViewHolder> {

    Context context;
    ArrayList<FireBaseModelClass> arrayList = new ArrayList<>();
    BottomSheetDialog bottomSheetDialog;


    public FireBaseAdapter(Context context, ArrayList<FireBaseModelClass> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public FireBaseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FireBaseModelClass fireBaseModelClass = arrayList.get(position);
        int colorCode = GetRandomColor();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            holder.itemLayout.setBackground(holder.itemView.getResources().getDrawable(colorCode,null));
        }
        holder.title.setText(fireBaseModelClass.getTitle());
        holder.content.setText(fireBaseModelClass.getContent());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean update;
                Intent intent = new Intent(context,CreateNoteActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("title",fireBaseModelClass.getTitle());
                intent.putExtra("content",fireBaseModelClass.getContent());
                intent.putExtra("int",111);

                context.startActivity(intent);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                 bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetTheme);
                View view = LayoutInflater.from(context).inflate(R.layout.bottomsheet_sample_layout,null);
                bottomSheetDialog.setContentView(view);
                LinearLayout delete,copy,share;
                delete = view.findViewById(R.id.deleteLayout);
                copy = view.findViewById(R.id.copyLayout);
                share = view.findViewById(R.id.shareLayout);

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String getTitle = fireBaseModelClass.getTitle();
                        DeleteNotes(getTitle);
                    }
                });
                try {
                    bottomSheetDialog.show();
                } catch(Exception e){
                    Log.d("serverRes",e.getMessage().toString());
                }
                return true;
            }
        });
    }

    private void DeleteNotes(String title) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("notes")
                .child(firebaseUser.getUid()).child("myNotes").child(title);
        databaseReference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(context, "Delete", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, ""+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            }
        });
    }

    private int GetRandomColor() {
        List<Integer> list = new ArrayList<>();
        list.add(R.color.one);
        list.add(R.color.two);
        list.add(R.color.three);
        list.add(R.color.teal_200);
        list.add(R.color.five);
        list.add(R.color.six);
        Random random = new Random();
        int number = random.nextInt(list.size());
        return list.get(number);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title,content;
        RelativeLayout itemLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleText);
            content = itemView.findViewById(R.id.contentText);
            itemLayout = itemView.findViewById(R.id.itemLayout);
        }
    }
}

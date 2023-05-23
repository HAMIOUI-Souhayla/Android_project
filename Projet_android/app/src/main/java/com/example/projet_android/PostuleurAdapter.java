package com.example.projet_android;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class PostuleurAdapter extends RecyclerView.Adapter<PostuleurAdapter.MyHolder> {

    Context context;
    List<Postuleurs> postuleurlist;
    FirebaseFirestore db;

    public PostuleurAdapter(Context context, List<Postuleurs> postuleurlist) {
        this.context = context;
        this.postuleurlist = postuleurlist;
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.postuleurs, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {
        final Postuleurs postuleur = postuleurlist.get(position);
        final String postuleurId = postuleur.getId();

        holder.adresse.setText(postuleur.getAdresse());
        holder.domaine.setText(postuleur.getDomaine());
        holder.desc.setText(postuleur.getDescription());


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(postuleur.getId());

        reference.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {


                                                Users users = snapshot.getValue(Users.class);


                                                holder.id.setText(users.getNom() + " " + users.getPrénom());

                                            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });







        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("friendid", postuleurId);
                context.startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {
        return postuleurlist.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        TextView id, domaine, adresse,desc;
        Button button;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.id);
            domaine = itemView.findViewById(R.id.domaine);
            adresse = itemView.findViewById(R.id.adresse);
            button = itemView.findViewById(R.id.button);
            desc = itemView.findViewById(R.id.desc);
        }
    }
}

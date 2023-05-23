package com.example.projet_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends AppCompatActivity {

    List<Chatslist> userlist;
    List<Users> mUsers;
    FirebaseAuth mAuth;
    TextView username;
    RecyclerView recyclerView;
    PostuleurAdaptertwo mAdapter;
    FirebaseUser firebaseUser;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_chats);
        toolbar = findViewById(R.id.toolbarmain);
        setSupportActionBar(toolbar);
        username = findViewById(R.id.usernameonmainactivity);
        userlist = new ArrayList<>();

        recyclerView = findViewById(R.id.chat_recyclerview_chatfrag);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chatslist")
                .child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userlist.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Chatslist chatslist = ds.getValue(Chatslist.class);
                    userlist.add(chatslist);
                }
                ChatsListings();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                if (users != null) {
                    username.setText(users.getNom() + " " + users.getPrénom());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ChatsListings() {
        mUsers = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Users user = ds.getValue(Users.class);
                    for (Chatslist chatslist : userlist) {
                        if (user.getId().equals(chatslist.getId())) {
                            mUsers.add(user);
                            break;
                        }
                    }
                }
                mAdapter = new PostuleurAdaptertwo(ChatsFragment.this, mUsers, true);
                recyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem postulerItem = menu.findItem(R.id.postuler);
        MenuItem demandeItem = menu.findItem(R.id.demande);
        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Postuleur");

        databaseReference.orderByChild("id").equalTo(currentUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    postulerItem.setVisible(false);
                    demandeItem.setVisible(true);
                } else {
                    postulerItem.setVisible(true);
                    demandeItem.setVisible(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Gérer les erreurs ici
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            mAuth.signOut();
            startActivity(new Intent(this, Sign_in_class.class));
            finish();
            return true;
        } else if (item.getItemId() == R.id.postuler) {
            startActivity(new Intent(this, Postuler.class));

            return true;
        } else if (item.getItemId() == R.id.bricoleurs) {
            startActivity(new Intent(this, Listepostuleur.class));

            return true;
        } else if (item.getItemId() == R.id.demande) {
            startActivity(new Intent(this, Suivi_demande.class));

            return true;
        } else if (item.getItemId() == R.id.profile) {
            startActivity(new Intent(this, UpdateActivity.class));

            return true;
        }
        else if (item.getItemId() == R.id.chat) {
            startActivity(new Intent(this, ChatsFragment.class));

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

package com.example.projet_android;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.projet_android.PostuleurAdapter;
import com.example.projet_android.Postuleurs;
import com.example.projet_android.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Listepostuleur extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Postuleurs> postuleursList;
    List<Postuleurs> filteredPostuleursList;
    PostuleurAdapter mAdapter;
    FirebaseUser firebaseUser;
    FirebaseAuth mAuth;
    Button button;
    DatabaseReference reference;

    TextView username;
    TextView postuler, info, suivi;
    Toolbar toolbar;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liste);
        username = findViewById(R.id.usernameonmainactivity);

        toolbar = findViewById(R.id.toolbarmain);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                if (users != null) {
                    username.setText(users.getNom() + " " + users.getPrénom()); // set the text of the user on textview in toolbar
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        recyclerView = findViewById(R.id.recycler1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        postuleursList = new ArrayList<>();
        filteredPostuleursList = new ArrayList<>();
        mAdapter = new PostuleurAdapter(Listepostuleur.this, filteredPostuleursList);
        recyclerView.setAdapter(mAdapter);

        displayUsers();

        searchView = findViewById(R.id.recherche);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterPostuleurs(newText);
                return true;
            }
        });
    }

    private void displayUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Postuleur");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postuleursList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Postuleurs postuleurs = ds.getValue(Postuleurs.class);

                    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                    if (postuleurs != null && firebaseUser != null && !postuleurs.getId().equals(firebaseUser.getUid())) {
                        postuleursList.add(postuleurs);
                    }
                }

                filteredPostuleursList.clear();
                filteredPostuleursList.addAll(postuleursList);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void filterPostuleurs(String query) {
        filteredPostuleursList.clear();

        if (TextUtils.isEmpty(query)) {
            filteredPostuleursList.addAll(postuleursList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Postuleurs postuleur : postuleursList) {

                String adresse = postuleur.getAdresse().toLowerCase();
                String domaine = postuleur.getDomaine().toLowerCase();

                if (adresse.contains(lowerCaseQuery) || domaine.contains(lowerCaseQuery)) {
                    filteredPostuleursList.add(postuleur);
                }
            }
        }

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem item = menu.findItem(R.id.recherche);
        if (item != null) {
            SearchView searchView = (SearchView) item.getActionView();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    filterPostuleurs(newText);
                    return true;
                }
            });
        }

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

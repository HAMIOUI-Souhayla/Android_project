package com.example.projet_android;

import static com.example.projet_android.Sign_in_class.mAuth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;


import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageActivity extends AppCompatActivity {
    String friendid,myid,message;
    EditText et_message;
    Button send;
    List<Chats> chatsList;
    MessageAdapter messageAdapter;
    RecyclerView recyclerView;
    //DatabaseReference reference;
    ValueEventListener seenlistener;
    DatabaseReference reference;
    FirebaseUser firebaseUser;
    TextView username;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        friendid = getIntent().getStringExtra("friendid"); // retreive the friendid when we click on the item
        Toast.makeText(this,"friendid:" +friendid,Toast.LENGTH_SHORT).show();
        username = findViewById(R.id.usernameonmainactivity);
        toolbar = findViewById(R.id.toolbarmain);
        setSupportActionBar(toolbar);
        //ajout apres
        send = findViewById(R.id.send_messsage_btn);
        et_message = findViewById(R.id.edit_message_text);
        recyclerView = findViewById(R.id.recyclerview_messages);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //myid = "dcrxxg29hGY8TidUlyKpsaZKPtX2";//who is login


        myid = firebaseUser.getUid();
        friendid = getIntent().getStringExtra("friendid"); // retreive the friendid when we click on the item
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(friendid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Users users = snapshot.getValue(Users.class);
                // set the text of the user on textivew in t
                //usernameonToolbar.setText(users.getUsername()); // set the text of the user on textivew in toolbar

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(friendid);

                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {


                        Users users = snapshot.getValue(Users.class);
                        username.setText(users.getNom()+" " + users.getPrénom());


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }

                });








                readMessages(myid, friendid);



            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
      seenMessage(friendid);
        et_message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                if (s.toString().length() > 0) {

                    send.setEnabled(true);

                } else {

                    send.setEnabled(false);


                }
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String text = et_message.getText().toString();

                if (!text.startsWith(" ")) {
                    et_message.getText().insert(0, " ");

                }

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                message = et_message.getText().toString();

               sendMessage(myid, friendid, message);//image





                et_message.setText(" ");


            }
        });
    }
   private void seenMessage(final String friendid) {

        reference = FirebaseDatabase.getInstance().getReference("Chat");


        seenlistener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds: snapshot.getChildren()) {

                    Chats chats = ds.getValue(Chats.class);

                    if (chats.getReciever().equals(myid) && chats.getSender().equals(friendid)) {

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        ds.getRef().updateChildren(hashMap);

                    }




                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        }

        private void sendMessage(final String myid, final String friendid, final String message) {


             final DatabaseReference  reference = FirebaseDatabase.getInstance().getReference();



                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("sender", myid);
                            hashMap.put("reciever", friendid);
                            hashMap.put("message", message);
                            //hashMap.put("isseen", false);

                            reference.child("Chat").push().setValue(hashMap);

            final DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Chatslist").child(myid).child(friendid);

            reference1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {


                    if (!snapshot.exists()) {


                        reference1.child("id").setValue(friendid);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });







        }
    private void readMessages(final String myid, final String friendid) {//image

        chatsList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chat");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatsList.clear();

                for (DataSnapshot ds: snapshot.getChildren()) {

                    Chats chats = ds.getValue(Chats.class);

                    if (chats.getSender().equals(myid) && chats.getReciever().equals(friendid) ||
                            chats.getSender().equals(friendid) && chats.getReciever().equals(myid)) {

                        chatsList.add(chats);
                    }

                    messageAdapter = new MessageAdapter(MessageActivity.this, chatsList);//image
                    recyclerView.setAdapter(messageAdapter);

                }

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

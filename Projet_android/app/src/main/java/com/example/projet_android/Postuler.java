package com.example.projet_android;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Postuler extends AppCompatActivity {
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private MenuItem selectedItem;

    TextView text;
    FirebaseUser firebaseUser;
    TextView username;
    Spinner et_domaine, et_adresse;
    FirebaseAuth mAuth;
    EditText et_desc;
    String domaine, adresse, desc;
    FirebaseAuth mPostul;
    Uri pdfUri;
    Uri pdf1Uri;
    Button button, Button1;
    DatabaseReference reference;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.postuler);
        username = findViewById(R.id.usernameonmainactivity);
        toolbar = findViewById(R.id.toolbarmain);
        setSupportActionBar(toolbar);
        text = (TextView) findViewById(R.id.textView);
        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Postuleur");
        mAuth = FirebaseAuth.getInstance();
        databaseReference.orderByChild("id").equalTo(currentUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    text.setText("Modifier ma demande de postulation");
                } else {
                    text.setText("Demande de postulation");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Gérer les erreurs ici
            }
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                Users users = snapshot.getValue(Users.class);

                username.setText(users.getNom() + " " + users.getPrénom()); // set the text of the user on textivew in toolbar


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        et_desc = findViewById(R.id.desc);
        et_domaine = findViewById(R.id.spinner);
        et_adresse = findViewById(R.id.adress);
        mPostul = FirebaseAuth.getInstance();

        button = findViewById(R.id.button2);

        button.setText("Charger votre carte nationale");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                String[] mimetypes = {"application/pdf", "image/*"};
                startActivityForResult(intent, 1);
            }
        });
        Button1 = findViewById(R.id.button1);

        Button1.setText("Charger votre CV");
        Button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                String[] mimetypes = {"application/pdf", "image/*"};
                startActivityForResult(intent, 2);
            }
        });
        Button spin1 = (Button) this.findViewById(R.id.button);
        spin1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String desc = et_desc.getText().toString();
                String domaine = et_domaine.getItemAtPosition(et_domaine.getSelectedItemPosition()).toString();
                String adresse = et_adresse.getItemAtPosition(et_adresse.getSelectedItemPosition()).toString();
                if (pdfUri == null) {
                    button.setError("Obligatoire");
                } else if (pdf1Uri == null) {
                    Button1.setError("Obligatoire");
                } else if (TextUtils.isEmpty(desc)) {
                    et_desc.setError("Obligatoire");
                } else {
                    uploadPdf(pdfUri, pdf1Uri);
                    stocker_post(domaine, desc, adresse, pdf1Uri, pdfUri);

                    AlertDialog.Builder builder = new AlertDialog.Builder(Postuler.this);
                    builder.setTitle("Demande  envoyée  avec  succès\n");

// Créer un bouton personnalisé
                    Button suivreButton = new Button(Postuler.this);
                    suivreButton.setText("Suivre ma demande");

                    suivreButton.setTextColor(Color.BLACK);


                    suivreButton.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    suivreButton.setGravity(Gravity.CENTER);

// Ajouter le bouton personnalisé à la boîte de dialogue
                    builder.setView(suivreButton);

// Définir le comportement du bouton
                    suivreButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Postuler.this, Suivi_demande.class);
                            startActivity(intent);

                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();


                }

            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 1 && resultCode == RESULT_OK) {
            pdfUri = data.getData();
            String filename = getFileNameFromUri(pdfUri);
            Button button = findViewById(R.id.button2);
            button.setText("votre carte nationale: \n" + filename);
            String filename1 = getFileNameFromUri(pdfUri);
        } else if (requestCode == 1 && resultCode != RESULT_OK) {
            button.setError("Obligatoire");
        }
        if (requestCode == 2 && resultCode == RESULT_OK) {
            pdf1Uri = data.getData();
            String filename = getFileNameFromUri(pdf1Uri);
            Button Button1 = findViewById(R.id.button1);
            Button1.setText("Votre CV:\n" + filename);
            String filename2 = getFileNameFromUri(pdf1Uri);
        } else if (requestCode == 2 && resultCode != RESULT_OK) {
            Button1.setError("Obligatoire");
        }
    }


    @SuppressLint("Range")
    private String getFileNameFromUri(Uri uri) {
        String filename = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                filename = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                cursor.close();
            }
        } else if (uri.getScheme().equals("file")) {
            filename = new File(uri.getPath()).getName();
        }
        return filename;
    }

    private void uploadPdf(Uri pdfUri, Uri pdf1Uri) {
        FirebaseUser postuleur = FirebaseAuth.getInstance().getCurrentUser();
        String filename1 = getFileNameFromUri(pdfUri);
        String filename2 = getFileNameFromUri(pdf1Uri);
        String filepath = "carte_natio/" + postuleur.getUid();
        String file1path = "cv/" + postuleur.getUid();

        // Get a reference to the Firebase Storage location where you want to upload the PDF
        StorageReference storageRef = storage.getReference().child(filepath);
        StorageReference storageRef1 = storage.getReference().child(file1path);
        // Upload the PDF file to Firebase Storage
        UploadTask uploadTask = storageRef.putFile(pdfUri);
        UploadTask uploadTask1 = storageRef1.putFile(pdf1Uri);
        // Set a listener to monitor the upload progress
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Log.d(TAG, "Upload is " + progress + "% done");
            }
        });

        // Set a listener to handle the upload success or failure
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "Upload success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Upload failed", e);
            }
        });


        uploadTask1.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Log.d(TAG, "Upload is " + progress + "% done");
            }
        });

        // Set a listener to handle the upload success or failure
        uploadTask1.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "Upload success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Upload failed", e);
            }
        });


    }


    private void stocker_post(String domaine, String desc, String adresse, Uri pdf1Uri, Uri pdfUri) {
        String filename1 = getFileNameFromUri(pdfUri);
        String filename2 = getFileNameFromUri(pdf1Uri);
        FirebaseUser postuler = mPostul.getCurrentUser();
        String sit = "En attente";
        reference = FirebaseDatabase.getInstance().getReference("Postuleur").child(postuler.getUid());
        Calendar calendar = Calendar.getInstance();
        Date currentTime = calendar.getTime();

        if (postuler != null) {

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("id", postuler.getUid());
            hashMap.put("domaine", domaine);
            hashMap.put("adresse", adresse);
            hashMap.put("description", desc);
            hashMap.put("CV", filename2);
            hashMap.put("carte_nationale", filename1);
            hashMap.put("situation", sit);
            hashMap.put("currentDateTime", currentTime.toString());

            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {


                    if (task.isSuccessful()) {

//                        Toast.makeText(Postuler.this, " Demande envoyée avec Succès,vous pouvez suivre votre demande", Toast.LENGTH_SHORT).show();


                    }
                }
            });


        }


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

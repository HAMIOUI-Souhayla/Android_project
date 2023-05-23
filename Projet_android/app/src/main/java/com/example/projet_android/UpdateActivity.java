package com.example.projet_android;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.google.firebase.storage.UploadTask;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UpdateActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_PICK_IMAGE = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 3;

    private EditText nomEditText;
    private EditText prenomEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private ImageButton photoProfilImageButton;
    private Spinner adresseSpinner;
    private Button validerButton;
    Toolbar toolbar;
    DatabaseReference reference;
    FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;

    private FirebaseUser mCurrentUser;
    TextView username;
    private FirebaseFirestore mFirestore;
    private StorageReference mStorageRef;
    private StorageReference mProfileImageRef;
    //  boolean passwordVisible;
    private Uri selectedImageUri;
    private Bitmap selectedImageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_personnelle);

        nomEditText = findViewById(R.id.nom);
        prenomEditText = findViewById(R.id.prenom);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.ancientpassword);
        photoProfilImageButton = findViewById(R.id.photoprofile);
        adresseSpinner = findViewById(R.id.adres);
        validerButton = findViewById(R.id.valider);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        username = findViewById(R.id.usernameonmainactivity);
        mFirestore = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        toolbar = findViewById(R.id.toolbarmain);
        setSupportActionBar(toolbar);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(UpdateActivity.this,
                R.array.villes_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adresseSpinner.setAdapter(adapter);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        loadUserData();



        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                Users users = snapshot.getValue(Users.class);

                username.setText(users.getNom()+" "+users.getPrénom()); // set the text of the user on textivew in toolbar


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        validerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             updateUserInfo();

            }
        });

        photoProfilImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkStoragePermission();
            }
        });




    }

















    private void loadUserData() {


        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {


                                                Users users = snapshot.getValue(Users.class);


                                                nomEditText.setText(users.getNom());
                                                prenomEditText.setText(users.getPrénom());
                                                emailEditText.setText(users.getEmail());
                                                String photoUrl = users.getPhotoDeProfil();
                                                String adresse = users.getAdresse();
                                                int position = ((ArrayAdapter<String>) adresseSpinner.getAdapter()).getPosition(adresse);
                      adresseSpinner.setSelection(position);

                                                if (photoUrl != null) {
                          // Utilisez Glide pour charger et afficher l'image dans la zone ImageButton
                           RequestOptions options = new RequestOptions()
                                   .placeholder(R.drawable.profile) ;// Image de remplacement en cas de chargement ou d'erreur
                          //  .error(R.drawable.error); // Image de remplacement en cas d'erreur de chargement

                          Glide.with(UpdateActivity.this)
                                   .load(photoUrl)
                                  .apply(options)
                                    .into(photoProfilImageButton);
                       }

                else {

                }







                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
    }


    private void updateUserInfo() {
        final String nom = nomEditText.getText().toString().trim();
        final String prenom = prenomEditText.getText().toString().trim();
        final String email = emailEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();
        final String adresse = adresseSpinner.getSelectedItem().toString();

        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs obligatoires", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mettre à jour les informations de l'utilisateur dans la base de données
        DocumentReference userRef = mFirestore.collection("Users").document(firebaseUser.getUid());
        userRef.update("nom", nom,
                        "prénom", prenom,
                        "email", email
                       )

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateActivity.this, "Erreur lors de la mise à jour des informations utilisateur", Toast.LENGTH_SHORT).show();
                    }
                });
        Toast.makeText(this, "Informations modifiées avec succès", Toast.LENGTH_SHORT).show();
    }



    private void redirectToEditListActivity() {
        Intent intent = new Intent(UpdateActivity.this, Listepostuleur.class);
        intent.putExtra("id", "list_edit");
        startActivity(intent);
        finish();
    }

    private void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PICK_IMAGE);
        } else {
            openGallery();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                try {
                    // Supprimer l'ancienne photo
                    deleteOldProfilePhoto();

                    selectedImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                    photoProfilImageButton.setImageBitmap(selectedImageBitmap);

                    // Enregistrer la nouvelle photo
                    saveNewProfilePhoto(selectedImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Uri capturedImage = (Uri) data.getExtras().get("data");
            if (capturedImage != null) {
                // Supprimer l'ancienne photo
                deleteOldProfilePhoto();

                try {
                    selectedImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), capturedImage);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                photoProfilImageButton.setImageBitmap(selectedImageBitmap);

                // Enregistrer la nouvelle photo
                saveNewProfilePhoto(capturedImage);
            }
        }
    }

    private void deleteOldProfilePhoto() {
        // Récupérer l'URL de l'ancienne photo à partir de la base de données
        DocumentReference userRef = mFirestore.collection("Users").document(mCurrentUser.getUid());
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String oldPhotoUrl = document.getString("photoDeProfil");
                        if (oldPhotoUrl != null) {
                            // Supprimer l'ancienne photo de Firebase Storage
                            StorageReference oldPhotoRef = FirebaseStorage.getInstance().getReferenceFromUrl(oldPhotoUrl);
                            oldPhotoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("UpdateActivity", "Ancienne photo supprimée avec succès");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("UpdateActivity", "Erreur lors de la suppression de l'ancienne photo : " + e.getMessage());
                                }
                            });
                        }
                    }
                } else {
                    Log.d("UpdateActivity", "Erreur lors de la récupération des données de l'utilisateur : ", task.getException());
                }
            }
        });
    }

    private void saveNewProfilePhoto(Uri imageUri) {
        // Enregistrer la nouvelle photo dans Firebase Storage
        StorageReference photoRef = mStorageRef.child("photos").child(mCurrentUser.getUid());
        photoRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Récupérer l'URL de la nouvelle photo téléchargée
                photoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Mettre à jour l'URL de la photo dans la base de données
                        String newPhotoUrl = uri.toString();
                        DocumentReference userRef = mFirestore.collection("Users").document(mCurrentUser.getUid());
                        userRef.update("photoDeProfil", newPhotoUrl)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("UpdateActivity", "Nouvelle photo enregistrée avec succès");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("UpdateActivity", "Erreur lors de l'enregistrement de la nouvelle photo dans la base de données : " + e.getMessage());
                                    }
                                });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("UpdateActivity", "Erreur lors du téléchargement de la nouvelle photo dans Firebase Storage : " + e.getMessage());
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
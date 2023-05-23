package com.example.projet_android;

import static android.content.ContentValues.TAG;
import static android.os.Environment.DIRECTORY_DOWNLOADS;
import de.hdodenhof.circleimageview.CircleImageView;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Suivi_demande extends AppCompatActivity {
    TextView username;
    Toolbar toolbar;
    FirebaseAuth mAuth;
    DatabaseReference reference;
    String delai;
    String name_cv,carte_natio_name;
    FirebaseUser firebaseUser;
    Button cv;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.suivi_demande);
        TextView domaine = (TextView) this.findViewById(R.id.textView10);
        TextView ville = (TextView) this.findViewById(R.id.textView2);
        TextView desc = (TextView) this.findViewById(R.id.textView17);
        TextView mes = (TextView) this.findViewById(R.id.textView18);
        Button modif= (Button) this.findViewById(R.id.button5);
        username = findViewById(R.id.usernameonmainactivity);
        toolbar = findViewById(R.id.toolbarmain);
        setSupportActionBar(toolbar);
        cv =(Button) this.findViewById(R.id.button3);
        Button cart_natio =(Button) this.findViewById(R.id.button4);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //

        //
        reference = FirebaseDatabase.getInstance().getReference("Postuleur").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null && snapshot.exists()) {
                    try {
                        Postuleurs postuleur = snapshot.getValue(Postuleurs.class);
                        domaine.setText(postuleur.getDomaine());
                        ville.setText(postuleur.getAdresse());
                       cv.setText(postuleur.getCV());
                        cart_natio.setText(postuleur.getCarte_nationale());
                        name_cv=postuleur.getCV();
                        carte_natio_name=postuleur.getCarte_nationale();
                        desc.setText(postuleur.getDescription());
// Obtenez la date actuelle
                        String currentDateTime = postuleur.getCurrentDateTime();

// Créez une SpannableString avec le texte complet
                        SpannableString spannableString = new SpannableString("Votre demande est en attente, elle serait envoyée 24 h après:\n" + currentDateTime + "\nSi ce délai est passé, vous n'avez plus le droit de modifier votre demande");

// Définissez le style de caractère gras pour la partie "postuleur.getCurrentDateTime()"
                        StyleSpan boldStyleSpan = new StyleSpan(Typeface.BOLD);
                        spannableString.setSpan(boldStyleSpan, spannableString.toString().indexOf(currentDateTime), spannableString.toString().indexOf(currentDateTime) + currentDateTime.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

// Définissez la couleur du texte pour la partie "postuleur.getCurrentDateTime()"
                        ForegroundColorSpan colorSpan = new ForegroundColorSpan(ContextCompat.getColor(Suivi_demande.this, R.color.dark_red));
                        spannableString.setSpan(colorSpan, spannableString.toString().indexOf(currentDateTime), spannableString.toString().indexOf(currentDateTime) + currentDateTime.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

// Appliquez la SpannableString à votre TextView
                        mes.setText(spannableString);
//                        // Récupération de la date et de l'heure stockées dans le champ "currentDateTime"
//                        String dateString = postuleur.getCurrentDateTime();
//                        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
//                        Date currentDate = format.parse(dateString);
//
//
//// Ajout de 24 heures à la date et l'heure actuelles
//                        Calendar calendar = Calendar.getInstance();
//                        calendar.setTime(currentDate);
//                        calendar.add(Calendar.HOUR_OF_DAY, 24);
//                        Date expiryDate = calendar.getTime();
//
//// Comparaison de la date et de l'heure actuelles avec la date et l'heure d'expiration
//                        Date now = new Date();
//                        if (now.after(expiryDate)) {
//                            modif.setVisibility(View.GONE);
//                        } else {
//                            modif.setVisibility(View.VISIBLE);
//                        }

                    } catch (Exception e) {
                        Log.e(TAG, "Error getting postuleur data", e);
                    }
                } else {
                    Log.e(TAG, "Snapshot is null or does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download();
            }


        });
        cart_natio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download2();
            }


        });
        modif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click(v);
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
    }

    public void download(){
        StorageReference    storageReference = FirebaseStorage.getInstance().getReference();
     StorageReference   ref=storageReference.child("cv/" +firebaseUser.getUid());
     ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
         @Override
         public void onSuccess(Uri uri) {
             String url =uri.toString();
             //cv.setText(name_cv);
             downloadFile1(Suivi_demande.this,"Cv/"+firebaseUser.getUid(),url);



         }
     }).addOnFailureListener(new OnFailureListener() {
         @Override
         public void onFailure(@NonNull Exception e) {

         }
     });
    }
    public void download2(){
        StorageReference    storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference   ref=storageReference.child("carte_natio/" +firebaseUser.getUid());
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url =uri.toString();
                //cv.setText(name_cv);
                downloadFile2(Suivi_demande.this,"Carte_natio/"+firebaseUser.getUid(), url);



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    public void downloadFile1(Context context, String fileName, String url) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name_cv );


        downloadManager.enqueue(request);
    }
    public void downloadFile2(Context context, String fileName, String url) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, carte_natio_name );


        downloadManager.enqueue(request);
    }
    private void click(View v) {
        Intent intent = new Intent(Suivi_demande.this, Postuler.class);
        this.startActivity(intent);
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

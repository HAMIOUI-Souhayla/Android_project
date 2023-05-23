package com.example.projet_android;
import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.appcompat.app.AppCompatActivity;

public class Signup_class extends AppCompatActivity {

    EditText et_nom, et_prenom, et_email,et_mot_passe,et_conf_mot_passe;
    String nom, prenom, email,mot_passe,conf_mot_passe;
    FirebaseAuth mAuth;
    boolean passwordVisible;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        Button sins = (Button) this.findViewById(R.id.sinscrire);

        sins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myClickB2(v);
            }


        });
        TextView cmpdej = (TextView) this.findViewById(R.id.cmptdeja);

        cmpdej.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myClickB(v);
            }
        });

        et_nom =findViewById(R.id.nom);
        et_prenom=findViewById(R.id.prenom);
         et_email=findViewById(R.id.email);

        et_mot_passe=findViewById(R.id.password);
        et_conf_mot_passe=findViewById(R.id.confirmpassword);
        mAuth = FirebaseAuth.getInstance();
        //pour le mot de passe Toogle
        et_mot_passe.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                final int Right=2;
                if(motionEvent.getAction()==MotionEvent.ACTION_UP)
                {
                    if(motionEvent.getRawX()>=et_mot_passe.getRight()-et_mot_passe.getCompoundDrawables()[Right].getBounds().width()){
                        int selection=et_mot_passe.getSelectionEnd();
                        if(passwordVisible){
                            et_mot_passe.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.baseline_visibility_off_24,0);

                            et_mot_passe.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible=false;
                        }else {
                            et_mot_passe.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.baseline_visibility_24,0);

                            et_mot_passe.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible=true;
                        }
                        et_mot_passe.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });

        //pour confirmer le mote de passe Toogle

        et_conf_mot_passe.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                final int Right=2;
                if(motionEvent.getAction()==MotionEvent.ACTION_UP)
                {
                    if(motionEvent.getRawX()>=et_conf_mot_passe.getRight()-et_conf_mot_passe.getCompoundDrawables()[Right].getBounds().width()){
                        int selection=et_conf_mot_passe.getSelectionEnd();
                        if(passwordVisible){
                            et_conf_mot_passe.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.baseline_visibility_off_24,0);

                            et_conf_mot_passe.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible=false;
                        }else {
                            et_conf_mot_passe.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.baseline_visibility_24,0);

                            et_conf_mot_passe.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible=true;
                        }
                        et_conf_mot_passe.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });
    }


    private void myClickB2(View v) {
        email = et_email.getText().toString();
        nom = et_nom.getText().toString();
        prenom = et_prenom.getText().toString();
        mot_passe = et_mot_passe.getText().toString();
        conf_mot_passe = et_conf_mot_passe.getText().toString();
        if (TextUtils.isEmpty(nom)) {
            et_nom.setError("Obligatoire");
        } else if (TextUtils.isEmpty(prenom)) {
            et_prenom.setError("Obligatoire");
        } else if (TextUtils.isEmpty(email)) {
            et_email.setError("Obligatoire");
        } else if (mot_passe.length() < 8) {
            et_mot_passe.setError("La longueur doit être supérieure à 8 caractères");
        } else if (conf_mot_passe.length() < 8) {
            et_conf_mot_passe.setError("La longueur doit être supérieure à 8 caractères");
        } else if (!(conf_mot_passe.toString().equals(mot_passe.toString()))) {
            et_conf_mot_passe.setError("Confirmation erronée");
        } else {
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
            Query emailQuery = usersRef.orderByChild("email").equalTo(email);
            emailQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // L'email existe déjà
                        Toast.makeText(getApplicationContext(), "Email déjà utilisé.", Toast.LENGTH_SHORT).show();
                    } else {
                        // L'email n'existe pas encore, donc on peut enregistrer l'utilisateur
                        registerUser(nom, prenom, email, mot_passe);
                        Toast.makeText(Signup_class.this, "Inscription réussite ", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Signup_class.this, Sign_in_class.class);
                        startActivity(intent);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAG, "onCancelled: " + error);
                }
            });
        }
    }


    private void myClickB(View v) {
        Intent intent = new Intent(Signup_class.this, Sign_in_class.class);
        this.startActivity(intent);
    }
    private void registerUser(String nom,String prenom, String email,String mot_passe) {

        mAuth.createUserWithEmailAndPassword(email, mot_passe).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {


                    FirebaseUser user = mAuth.getCurrentUser();

                    reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());


                    if (user!=null ) {

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("nom", nom);
                        hashMap.put("prénom", prenom);
                        hashMap.put("email", email);
                        hashMap.put("id", user.getUid());
                        hashMap.put("role", "default");
                        hashMap.put("status", "offline");
                        hashMap.put("ImageURL", "default");



                        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {


                                if (task.isSuccessful()) {

                                    Toast.makeText(Signup_class.this, "Inscription réussite ", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }


                }
            }
        });

    }


  }

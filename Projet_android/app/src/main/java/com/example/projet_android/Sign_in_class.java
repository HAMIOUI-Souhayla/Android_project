package com.example.projet_android;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rengwuxian.materialedittext.MaterialEditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


public class Sign_in_class extends AppCompatActivity {

    EditText et_pass, et_email;
    boolean passwordVisible;
    Button cnxBtn;
    TextView cree_c;
    String email, mot_passe;
    static FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);

        cree_c = (TextView) this.findViewById(R.id.creecmpt);
        cnxBtn = (Button) this.findViewById(R.id.cnxbutton);
        et_pass = findViewById(R.id.password);
        et_email = findViewById(R.id.email);
        mAuth = FirebaseAuth.getInstance();

        //Creer compte
        cree_c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myClickB2(v);
            }

        });
        //pour la connexion
        cnxBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = et_email.getText().toString();
                mot_passe = et_pass.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    et_email.setError("Required");
                } else if (TextUtils.isEmpty(mot_passe)) {
                    et_pass.setError("Required");
                } else {
                    LoginMeIn(email, mot_passe);
                }

            }

        });

        //pour mote de passe
        et_pass.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                final int Right=2;
                if(motionEvent.getAction()==MotionEvent.ACTION_UP)
                {
                    if(motionEvent.getRawX()>=et_pass.getRight()-et_pass.getCompoundDrawables()[Right].getBounds().width()){
                        int selection=et_pass.getSelectionEnd();
                        if(passwordVisible){
                            et_pass.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.baseline_visibility_off_24,0);

                            et_pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible=false;
                        }else {
                            et_pass.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.baseline_visibility_24,0);

                            et_pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible=true;
                        }
                        et_pass.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });

    }

    private void myClickB2(View v) {
        Intent intent = new Intent(Sign_in_class.this, Signup_class.class);
        startActivity(intent);
    }

    private void LoginMeIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user.isEmailVerified()) {
                        Intent intent = new Intent(Sign_in_class.this, Listepostuleur.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
//                        finish();
                        Toast.makeText(Sign_in_class.this, "Connexion réussite", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Sign_in_class.this, "Envoi de la Vérification à " + user.getEmail(), Toast.LENGTH_SHORT).show();

                        sendVerificationEmail();
                    }
                } else {
                    Toast.makeText(Sign_in_class.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendVerificationEmail() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(Sign_in_class.this, "Vérifier votre email", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Sign_in_class.this, "La Vérification  est déja envoyée à", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }}
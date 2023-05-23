package com.example.projet_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class Main extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Projet_android);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bienvenue_page);
      Button suivant = (Button) this.findViewById(R.id.suivant);

       suivant.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View v) {
               myClickB2(v);
            }


        });

    }
    private void myClickB2(View v) {
        Intent intent = new Intent(Main.this, Sign_in_class.class);
        this.startActivity(intent);
    }
    }



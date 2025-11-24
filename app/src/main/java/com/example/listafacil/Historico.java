package com.example.listafacil;

import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Historico extends AppCompatActivity {

    private Button voltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_historico);

        //Achar id
        voltar = findViewById(R.id.btnVoltar);

        //onclick
        voltar.setOnClickListener(view -> finish());
    }//oncrete
}//
// fim da class
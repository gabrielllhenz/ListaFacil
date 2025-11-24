package com.example.listafacil;

import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MenuListas extends AppCompatActivity {

    private Button voltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_listas);
        //Achar id
        voltar = findViewById(R.id.btnVoltar);

        //onclick
        voltar.setOnClickListener(view -> finish());
    }//oncreate


}//fim da class
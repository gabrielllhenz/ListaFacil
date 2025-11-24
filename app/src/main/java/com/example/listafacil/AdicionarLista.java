package com.example.listafacil;

import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class AdicionarLista extends AppCompatActivity {

    private Button salvar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_adicionar_lista);

        salvar = findViewById(R.id.btnSalvar);

        salvar.setOnClickListener(view -> finish());
    }
}
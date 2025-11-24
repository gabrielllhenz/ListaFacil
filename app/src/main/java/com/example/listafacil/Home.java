package com.example.listafacil;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class Home extends AppCompatActivity {

    private CardView addLista, selLista, historico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        addLista = findViewById(R.id.btnAddLista);
        selLista = findViewById(R.id.btnSelecionarLista);
        historico = findViewById(R.id.btnHistorico);

        addLista.setOnClickListener(view -> abrirAddLista());
        selLista.setOnClickListener(view -> abrirSelecionarLista());
        historico.setOnClickListener(view -> abrirHistorico());
    }//oncreate

    private void abrirAddLista(){
        Intent gremio = new Intent(this, AdicionarLista.class);
        startActivity(gremio);
    }

    private void abrirSelecionarLista(){
        Intent gremio = new Intent(this, MenuListas.class);
        startActivity(gremio);
    }

    private void abrirHistorico(){
        Intent gremio = new Intent(this, Historico.class);
        startActivity(gremio);
    }


}//fim da class
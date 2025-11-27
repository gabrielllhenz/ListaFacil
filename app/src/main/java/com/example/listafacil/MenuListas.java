package com.example.listafacil;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MenuListas extends AppCompatActivity {

    private Button voltar;
    private RecyclerView rvLists;
    private MeuBancoHelper dbHelper;
    private ListaCompraAdapter adapter;
    private List<ListaCompra> listas = new ArrayList<>();

    @Override
    protected void onResume() {
        super.onResume();
        carregarListasDoBanco();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_listas);

        voltar = findViewById(R.id.btnVoltar);
        rvLists = findViewById(R.id.rvLists);
        dbHelper = new MeuBancoHelper(this);

        voltar.setOnClickListener(view -> finish());

        carregarListasDoBanco();

        adapter = new ListaCompraAdapter(this, listas);
        rvLists.setAdapter(adapter);
        rvLists.setLayoutManager(new LinearLayoutManager(this));
    }



    private void carregarListasDoBanco() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        listas.clear();

        Cursor cursorListas = db.rawQuery("SELECT * FROM listas ORDER BY id DESC", null);
        if (cursorListas.moveToFirst()) {
            do {
                int id = cursorListas.getInt(cursorListas.getColumnIndexOrThrow("id"));
                String titulo = cursorListas.getString(cursorListas.getColumnIndexOrThrow("titulo"));
                String data = cursorListas.getString(cursorListas.getColumnIndexOrThrow("data"));

                List<ItemLista> itens = new ArrayList<>();
                Cursor cursorItens = db.rawQuery(
                        "SELECT descricao, quantidade, unidade, categoria FROM itens WHERE lista_id = ?",
                        new String[]{String.valueOf(id)}
                );

                if (cursorItens.moveToFirst()) {
                    do {
                        String descricao = cursorItens.getString(cursorItens.getColumnIndexOrThrow("descricao"));
                        int quantidade = cursorItens.getInt(cursorItens.getColumnIndexOrThrow("quantidade"));
                        String unidade = cursorItens.getString(cursorItens.getColumnIndexOrThrow("unidade"));
                        String categoria = cursorItens.getString(cursorItens.getColumnIndexOrThrow("categoria"));

                        android.util.Log.d("DEBUG_ITEM", "desc=" + descricao +
                                " q=" + quantidade +
                                " un=" + unidade +
                                " cat=" + categoria);

                        itens.add(new ItemLista(descricao, categoria, unidade, quantidade));
                    } while (cursorItens.moveToNext());
                }
                cursorItens.close();

                listas.add(new ListaCompra(id, titulo, data, itens));
            } while (cursorListas.moveToNext());
        }
        cursorListas.close();
    }


}//fim da class
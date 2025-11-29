package com.example.listafacil;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MeuBancoHelper extends SQLiteOpenHelper {
    public MeuBancoHelper(Context context) {
        super(context, "MeuBanco.db", null, 3); // VERSÃO 3 <- MUDE AQUI
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE listas (id INTEGER PRIMARY KEY AUTOINCREMENT, titulo TEXT, data TEXT)");
        db.execSQL("CREATE TABLE itens (id INTEGER PRIMARY KEY AUTOINCREMENT, lista_id INTEGER, descricao TEXT, quantidade INTEGER, unidade TEXT, categoria TEXT)");
        db.execSQL("CREATE TABLE historico (id INTEGER PRIMARY KEY AUTOINCREMENT, lista_id INTEGER, data_compra TEXT, total REAL)");
        db.execSQL("CREATE TABLE historico_itens (id INTEGER PRIMARY KEY AUTOINCREMENT, historico_id INTEGER, descricao TEXT, quantidade INTEGER, unidade TEXT, preco REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Garantir que as tabelas existam
        db.execSQL("CREATE TABLE IF NOT EXISTS historico (id INTEGER PRIMARY KEY AUTOINCREMENT, lista_id INTEGER, data_compra TEXT, total REAL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS historico_itens (id INTEGER PRIMARY KEY AUTOINCREMENT, historico_id INTEGER, descricao TEXT, quantidade INTEGER, unidade TEXT, preco REAL)");
    }
}

package com.example.listafacil;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MeuBancoHelper extends SQLiteOpenHelper {
    public MeuBancoHelper(Context context) {
        super(context, "MeuBanco.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE listas (id INTEGER PRIMARY KEY AUTOINCREMENT, titulo TEXT, data TEXT)");
        db.execSQL("CREATE TABLE itens (id INTEGER PRIMARY KEY AUTOINCREMENT, lista_id INTEGER, descricao TEXT, quantidade INTEGER, unidade TEXT, categoria TEXT)");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS listas");
        db.execSQL("DROP TABLE IF EXISTS itens");
        onCreate(db);
    }

}


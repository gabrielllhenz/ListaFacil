package com.example.listafacil;

import java.util.List;

public class ListaCompra {
    public int id;
    public String titulo;
    public String data;
    public List<ItemLista> itens;

    public ListaCompra(int id, String titulo, String data, List<ItemLista> itens) {
        this.id = id;
        this.titulo = titulo;
        this.data = data;
        this.itens = itens;
    }
}


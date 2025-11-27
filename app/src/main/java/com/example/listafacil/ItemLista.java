package com.example.listafacil;

import java.io.Serializable;

public class ItemLista implements Serializable {
    public String nome;
    public String categoria;
    public String unidade;
    public int quantidade;

    public ItemLista(String nome, String categoria, String unidade, int quantidade) {
        this.nome = nome;
        this.categoria = categoria;
        this.unidade = unidade;
        this.quantidade = quantidade;
    }
}

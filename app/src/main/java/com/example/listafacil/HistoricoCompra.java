package com.example.listafacil;

import java.util.ArrayList;

public class HistoricoCompra {
    public int id;
    public String nomeLista;
    public String dataCompra;
    public double total;
    public ArrayList<ItemHistorico> itens;

    public HistoricoCompra(int id, String nomeLista, String dataCompra, double total) {
        this.id = id;
        this.nomeLista = nomeLista;
        this.dataCompra = dataCompra;
        this.total = total;
        this.itens = new ArrayList<>();
    }

    public static class ItemHistorico {
        public String descricao;
        public int quantidade;
        public String unidade;
        public double preco;

        public ItemHistorico(String descricao, int quantidade, String unidade, double preco) {
            this.descricao = descricao;
            this.quantidade = quantidade;
            this.unidade = unidade;
            this.preco = preco;
        }
    }
}

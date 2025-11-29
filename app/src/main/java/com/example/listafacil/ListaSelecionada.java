package com.example.listafacil;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.HorizontalScrollView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class ListaSelecionada extends AppCompatActivity {

    private TextView tvListName, tvCreatedDate;
    private RecyclerView rvItems;
    private LinearLayout llTotals, llHistoricoSection, btnToggleHistorico;
    private HorizontalScrollView hsvTotals;
    private Button btnFinalize;
    private ImageView ivCart, ivBack, ivExpandIcon;
    private SwitchCompat switchPrecoObrigatorio;
    private ArrayList<ItemLista> itens = new ArrayList<>();
    private ItemCheckAdapter adapter;
    private int listaId;
    private String titulo;
    private String data;
    private boolean historicoExpandido = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_selecionada);

        // Receber dados da Intent
        listaId = getIntent().getIntExtra("listaId", -1);
        titulo = getIntent().getStringExtra("titulo");
        data = getIntent().getStringExtra("data");
        itens = (ArrayList<ItemLista>) getIntent().getSerializableExtra("itens");

        // Find views
        tvListName = findViewById(R.id.tvListName);
        tvCreatedDate = findViewById(R.id.tvCreatedDate);
        rvItems = findViewById(R.id.rvItems);
        llTotals = findViewById(R.id.llTotals);
        llHistoricoSection = findViewById(R.id.llHistoricoSection);
        hsvTotals = findViewById(R.id.hsvTotals);
        btnToggleHistorico = findViewById(R.id.btnToggleHistorico);
        ivExpandIcon = findViewById(R.id.ivExpandIcon);
        btnFinalize = findViewById(R.id.btnFinalize);
        ivCart = findViewById(R.id.ivCart);
        ivBack = findViewById(R.id.ivBack);
        switchPrecoObrigatorio = findViewById(R.id.switchPrecoObrigatorio);

        // Preencher dados
        tvListName.setText(titulo);
        tvCreatedDate.setText("Criado " + data);

        // Configurar RecyclerView
        adapter = new ItemCheckAdapter(this, itens);
        adapter.setOnItemChangedListener(() -> atualizarCardsHistorico());
        rvItems.setAdapter(adapter);
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        // Iniciar com histórico minimizado
        historicoExpandido = false;
        hsvTotals.setVisibility(View.GONE);
        ivExpandIcon.setImageResource(R.drawable.ic_expand_more);

        // Carregar histórico (se existir)
        carregarHistorico();

        // Botão toggle expandir/minimizar
        btnToggleHistorico.setOnClickListener(v -> toggleHistorico());

        // Botões
        ivBack.setOnClickListener(v -> finish());
        ivCart.setOnClickListener(v -> finish());
        btnFinalize.setOnClickListener(v -> finalizarLista());
    }


    private void toggleHistorico() {
        historicoExpandido = !historicoExpandido;

        if (historicoExpandido) {
            hsvTotals.setVisibility(View.VISIBLE);
            ivExpandIcon.setImageResource(R.drawable.ic_expand_less);
        } else {
            hsvTotals.setVisibility(View.GONE);
            ivExpandIcon.setImageResource(R.drawable.ic_expand_more);
        }
    }

    private void atualizarCardsHistorico() {
        carregarHistorico();
    }

    private void finalizarLista() {
        double total = 0;
        ArrayList<ItemLista> itensComprados = new ArrayList<>();
        boolean temItemSemPreco = false;

        for (ItemLista item : itens) {
            if (item.comprado) {
                itensComprados.add(item);
                if (item.preco > 0) {
                    total += item.preco * item.quantidade;
                } else {
                    temItemSemPreco = true;
                }
            }
        }

        if (itensComprados.isEmpty()) {
            Toast.makeText(this, "Nenhum item foi marcado como comprado!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (switchPrecoObrigatorio.isChecked() && temItemSemPreco) {
            Toast.makeText(this, "Preencha os preços de todos os itens marcados!", Toast.LENGTH_LONG).show();
            return;
        }

        MeuBancoHelper dbHelper = new MeuBancoHelper(this);
        SQLiteDatabase db = null;

        try {
            db = dbHelper.getWritableDatabase();
            db.beginTransaction(); // <-- INICIA TRANSAÇÃO

            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
            String dataCompra = sdf.format(new java.util.Date());

            ContentValues valoresHistorico = new ContentValues();
            valoresHistorico.put("lista_id", listaId);
            valoresHistorico.put("data_compra", dataCompra);
            valoresHistorico.put("total", total);

            long historicoId = db.insert("historico", null, valoresHistorico);

            android.util.Log.d("DEBUG_SALVAR", "Histórico salvo com ID: " + historicoId);
            android.util.Log.d("DEBUG_SALVAR", "Data: " + dataCompra + " | Total: " + total);

            for (ItemLista item : itensComprados) {
                ContentValues valoresItem = new ContentValues();
                valoresItem.put("historico_id", historicoId);
                valoresItem.put("descricao", item.nome);
                valoresItem.put("quantidade", item.quantidade);
                valoresItem.put("unidade", item.unidade);
                valoresItem.put("preco", item.preco);
                db.insert("historico_itens", null, valoresItem);
            }

            db.setTransactionSuccessful(); // <-- MARCA COMO SUCESSO

            Toast.makeText(this,
                    "Compra finalizada!\nTotal: R$ " + String.format("%.2f", total),
                    Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            android.util.Log.e("DEBUG_SALVAR", "Erro ao salvar: " + e.getMessage());
            Toast.makeText(this, "Erro ao salvar compra!", Toast.LENGTH_SHORT).show();
        } finally {
            if (db != null) {
                db.endTransaction(); // <-- FINALIZA TRANSAÇÃO
                db.close();
            }
        }

        finish();
    }



    private void carregarHistorico() {
        MeuBancoHelper dbHelper = new MeuBancoHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        llTotals.removeAllViews();

        // Verificar se há itens marcados
        boolean temItensMarcados = false;
        for (ItemLista item : itens) {
            if (item.comprado) {
                temItensMarcados = true;
                break;
            }
        }

        if (!temItensMarcados) {
            llHistoricoSection.setVisibility(View.GONE);
            db.close();
            return;
        }

        llHistoricoSection.setVisibility(View.VISIBLE);

        // Card 1 (ESQUERDA): Compra atual (itens marcados agora)
        ArrayList<String> itensAtuais = new ArrayList<>();
        double totalAtual = 0;

        for (ItemLista item : itens) {
            if (item.comprado) {
                itensAtuais.add(item.nome);
                if (item.preco > 0) {
                    itensAtuais.add(String.format("R$ %.2f", item.preco * item.quantidade));
                    totalAtual += item.preco * item.quantidade;
                } else {
                    itensAtuais.add("R$ --");
                }
            }
        }

        // Criar card da compra atual (esquerda)
        if (!itensAtuais.isEmpty()) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM", java.util.Locale.getDefault());
            String dataAtual = sdf.format(new java.util.Date());
            criarCardHistorico(dataAtual + " (Atual)", itensAtuais, totalAtual);
        }

        // Card 2 (DIREITA): Última compra (histórico) - apenas itens que também estão marcados agora
        Cursor cursorHistorico = db.rawQuery(
                "SELECT * FROM historico WHERE lista_id = ? ORDER BY id DESC LIMIT 1",
                new String[]{String.valueOf(listaId)}
        );

        if (cursorHistorico.moveToFirst()) {
            int historicoId = cursorHistorico.getInt(cursorHistorico.getColumnIndexOrThrow("id"));
            String dataCompra = cursorHistorico.getString(cursorHistorico.getColumnIndexOrThrow("data_compra"));

            // Buscar itens desse histórico
            Cursor cursorItens = db.rawQuery(
                    "SELECT * FROM historico_itens WHERE historico_id = ?",
                    new String[]{String.valueOf(historicoId)}
            );

            ArrayList<String> itensTexto = new ArrayList<>();
            double totalHistorico = 0;

            while (cursorItens.moveToNext()) {
                String desc = cursorItens.getString(cursorItens.getColumnIndexOrThrow("descricao"));
                int qtd = cursorItens.getInt(cursorItens.getColumnIndexOrThrow("quantidade"));
                double preco = cursorItens.getDouble(cursorItens.getColumnIndexOrThrow("preco"));

                // Verificar se este item está marcado na compra atual
                boolean itemMarcadoAtual = false;
                for (ItemLista itemAtual : itens) {
                    if (itemAtual.nome.equals(desc) && itemAtual.comprado) {
                        itemMarcadoAtual = true;
                        break;
                    }
                }

                if (itemMarcadoAtual) {
                    itensTexto.add(desc);
                    itensTexto.add(String.format("R$ %.2f", preco * qtd));
                    totalHistorico += preco * qtd;
                }
            }
            cursorItens.close();

            // Criar card do histórico (direita)
            if (!itensTexto.isEmpty()) {
                criarCardHistorico(dataCompra, itensTexto, totalHistorico);
            }
        }
        cursorHistorico.close();
        db.close();
    }

    private void criarCardHistorico(String data, ArrayList<String> itensTexto, double total) {
        // Inflar o card
        View cardView = getLayoutInflater().inflate(R.layout.card_historico, llTotals, false);

        TextView tvDataTitulo = cardView.findViewById(R.id.tvDataTitulo);
        LinearLayout llItensHistorico = cardView.findViewById(R.id.llItensHistorico);
        TextView tvTotalHistorico = cardView.findViewById(R.id.tvTotalHistorico);

        tvDataTitulo.setText("TOTAL " + data);
        if (total > 0) {
            tvTotalHistorico.setText(String.format("R$ %.2f", total));
        } else {
            tvTotalHistorico.setText("R$ --");
        }

        // Adicionar itens dinamicamente (agora em linhas horizontais: nome | preço)
        for (int i = 0; i < itensTexto.size(); i += 2) {
            String nomeItem = itensTexto.get(i);
            String precoItem = (i + 1 < itensTexto.size()) ? itensTexto.get(i + 1) : "";

            // Criar LinearLayout horizontal para cada item
            LinearLayout itemRow = new LinearLayout(this);
            itemRow.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            rowParams.setMargins(0, 0, 0, (int) (4 * getResources().getDisplayMetrics().density));
            itemRow.setLayoutParams(rowParams);

            // TextView para o nome (à esquerda)
            TextView tvNome = new TextView(this);
            tvNome.setText(nomeItem);
            tvNome.setTextSize(11);
            tvNome.setTextColor(getResources().getColor(R.color.text_white));
            LinearLayout.LayoutParams nomeParams = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1.0f
            );
            tvNome.setLayoutParams(nomeParams);

            // TextView para o preço (à direita)
            TextView tvPreco = new TextView(this);
            tvPreco.setText(precoItem);
            tvPreco.setTextSize(11);
            tvPreco.setTextColor(getResources().getColor(R.color.text_white));
            tvPreco.setGravity(android.view.Gravity.END);
            LinearLayout.LayoutParams precoParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            tvPreco.setLayoutParams(precoParams);

            itemRow.addView(tvNome);
            itemRow.addView(tvPreco);
            llItensHistorico.addView(itemRow);
        }

        // Adicionar o card ao LinearLayout com largura fixa
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                (int) (220 * getResources().getDisplayMetrics().density), // Largura fixa de 220dp
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        int margin = (int) (8 * getResources().getDisplayMetrics().density);
        cardParams.setMargins(margin, 0, margin, 0);
        cardView.setLayoutParams(cardParams);

        llTotals.addView(cardView);
    }

}

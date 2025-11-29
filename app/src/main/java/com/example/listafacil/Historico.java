package com.example.listafacil;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class Historico extends AppCompatActivity {

    private TextView tvMonthYear, tvNoHistory;
    private ImageView ivBack, ivCart, ivPrevMonth, ivNextMonth;
    private ViewPager2 vpHistorico;
    private HistoricoAdapter adapter;

    private Calendar calendarioAtual;
    private Calendar calendarioHoje;
    private ArrayList<HistoricoCompra> historicos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);


        // Inicializar calendários
        calendarioAtual = Calendar.getInstance();
        calendarioHoje = Calendar.getInstance();

        // Find views
        tvMonthYear = findViewById(R.id.tvMonthYear);
        tvNoHistory = findViewById(R.id.tvNoHistory);
        ivBack = findViewById(R.id.ivBack);
        ivCart = findViewById(R.id.ivCart);
        ivPrevMonth = findViewById(R.id.ivPrevMonth);
        ivNextMonth = findViewById(R.id.ivNextMonth);
        vpHistorico = findViewById(R.id.vpHistorico);

        // Configurar ViewPager2
        adapter = new HistoricoAdapter(this, historicos);
        vpHistorico.setAdapter(adapter);

        // Atualizar display
        atualizarMesAno();
        carregarHistorico();
        debugBanco(); // <- ADICIONE ESTA LINHA

        // Botões
        ivBack.setOnClickListener(v -> finish());

        // Botões
        ivBack.setOnClickListener(v -> finish());
        ivCart.setOnClickListener(v -> finish());

        // BOTÃO ESQUERDA: Volta no tempo (mês anterior)
        ivPrevMonth.setOnClickListener(v -> {
            calendarioAtual.add(Calendar.MONTH, -1);
            atualizarMesAno();
            carregarHistorico();
        });

        // BOTÃO DIREITA: Avança no tempo (próximo mês) - até o mês atual
        ivNextMonth.setOnClickListener(v -> {
            // Não permite ir para o futuro
            Calendar proximoMes = (Calendar) calendarioAtual.clone();
            proximoMes.add(Calendar.MONTH, 1);

            if (proximoMes.get(Calendar.YEAR) < calendarioHoje.get(Calendar.YEAR) ||
                    (proximoMes.get(Calendar.YEAR) == calendarioHoje.get(Calendar.YEAR) &&
                            proximoMes.get(Calendar.MONTH) <= calendarioHoje.get(Calendar.MONTH))) {
                calendarioAtual.add(Calendar.MONTH, 1);
                atualizarMesAno();
                carregarHistorico();
            }
        });
    }


    private void atualizarMesAno() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", new Locale("pt", "BR"));
        String mesAno = sdf.format(calendarioAtual.getTime());
        // Capitalizar primeira letra
        tvMonthYear.setText(mesAno.substring(0, 1).toUpperCase() + mesAno.substring(1));
    }

    private void carregarHistorico() {
        historicos.clear();

        MeuBancoHelper dbHelper = new MeuBancoHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Pegar mês e ano do calendário
        int mes = calendarioAtual.get(Calendar.MONTH) + 1; // Calendar.MONTH é 0-based
        int ano = calendarioAtual.get(Calendar.YEAR);

        // Formato MM/yyyy para comparação (ex: "11/2025")
        String mesAnoFiltro = String.format(Locale.getDefault(), "%02d/%d", mes, ano);

        android.util.Log.d("DEBUG_HISTORICO", "Buscando histórico para: " + mesAnoFiltro);

        // Buscar históricos do mês/ano selecionado
        // data_compra está no formato dd/MM/yyyy (ex: 29/11/2025)
        // substr no SQLite começa do índice 1, então substr(data_compra, 4, 7) pega MM/yyyy
        Cursor cursorHistorico = db.rawQuery(
                "SELECT h.*, l.titulo FROM historico h " +
                        "INNER JOIN listas l ON h.lista_id = l.id " +
                        "WHERE substr(h.data_compra, 4, 7) = ? " +
                        "ORDER BY h.id DESC",
                new String[]{mesAnoFiltro}
        );

        android.util.Log.d("DEBUG_HISTORICO", "Registros encontrados: " + cursorHistorico.getCount());

        while (cursorHistorico.moveToNext()) {
            int historicoId = cursorHistorico.getInt(cursorHistorico.getColumnIndexOrThrow("id"));
            String nomeLista = cursorHistorico.getString(cursorHistorico.getColumnIndexOrThrow("titulo"));
            String dataCompra = cursorHistorico.getString(cursorHistorico.getColumnIndexOrThrow("data_compra"));
            double total = cursorHistorico.getDouble(cursorHistorico.getColumnIndexOrThrow("total"));

            android.util.Log.d("DEBUG_HISTORICO", "Compra encontrada: " + nomeLista + " - " + dataCompra);

            HistoricoCompra historico = new HistoricoCompra(historicoId, nomeLista, dataCompra, total);

            // Buscar itens desse histórico
            Cursor cursorItens = db.rawQuery(
                    "SELECT * FROM historico_itens WHERE historico_id = ?",
                    new String[]{String.valueOf(historicoId)}
            );

            while (cursorItens.moveToNext()) {
                String desc = cursorItens.getString(cursorItens.getColumnIndexOrThrow("descricao"));
                int qtd = cursorItens.getInt(cursorItens.getColumnIndexOrThrow("quantidade"));
                String unidade = cursorItens.getString(cursorItens.getColumnIndexOrThrow("unidade"));
                double preco = cursorItens.getDouble(cursorItens.getColumnIndexOrThrow("preco"));

                historico.itens.add(new HistoricoCompra.ItemHistorico(desc, qtd, unidade, preco));
            }
            cursorItens.close();

            historicos.add(historico);
        }
        cursorHistorico.close();
        db.close();

        // Atualizar adapter
        adapter.notifyDataSetChanged();

        // Mostrar/ocultar mensagem
        if (historicos.isEmpty()) {
            tvNoHistory.setVisibility(View.VISIBLE);
            vpHistorico.setVisibility(View.GONE);
        } else {
            tvNoHistory.setVisibility(View.GONE);
            vpHistorico.setVisibility(View.VISIBLE);
        }
    }

    private void debugBanco() {
        MeuBancoHelper dbHelper = new MeuBancoHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Ver TUDO sem filtro
        Cursor cursor = db.rawQuery("SELECT * FROM historico", null);

        android.util.Log.d("DEBUG_HISTORICO", "===== TODAS AS COMPRAS NO BANCO =====");
        android.util.Log.d("DEBUG_HISTORICO", "Total de compras: " + cursor.getCount());

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            int listaId = cursor.getInt(cursor.getColumnIndexOrThrow("lista_id"));
            String data = cursor.getString(cursor.getColumnIndexOrThrow("data_compra"));
            double total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));

            android.util.Log.d("DEBUG_HISTORICO", "ID: " + id + " | Lista ID: " + listaId + " | Data: " + data + " | Total: " + total);

            // Testar substr
            if (data != null && data.length() >= 10) {
                String mesAno = data.substring(3, 10); // Pegar MM/yyyy manualmente
                android.util.Log.d("DEBUG_HISTORICO", "  -> Mês/Ano extraído: " + mesAno);
            }
        }

        cursor.close();
        db.close();
    }



}

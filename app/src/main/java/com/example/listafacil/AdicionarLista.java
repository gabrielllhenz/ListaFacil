package com.example.listafacil;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;

public class AdicionarLista extends AppCompatActivity {

    private Button salvar, salvarItem;
    private TextView addItem;
    private MeuBancoHelper dbHelper;
    private ArrayList<ItemLista> itens = new ArrayList<>();
    private ItemAdapter adapter;
    private RecyclerView rvItems;
    private ImageView ivCart;
    private EditText etDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_adicionar_lista);

        //find view by id
        salvar = findViewById(R.id.btnSalvar);
        addItem = findViewById(R.id.tvAddItems);
        rvItems = findViewById(R.id.rvItems);
        adapter = new ItemAdapter(this, itens);
        rvItems.setAdapter(adapter);
        rvItems.setLayoutManager(new LinearLayoutManager(this));
        ivCart = findViewById(R.id.ivCart);
        etDate = findViewById(R.id.etDate);
        etDate.setFocusable(false);

        //adapter
        adapter.setOnEditClickListener((item, position) -> {
            abrirAdicionarItem(item, position);
        });

        //botoes
        salvar.setOnClickListener(view -> salvarLista());
        addItem.setOnClickListener(view -> abrirAdicionarItem());
        ivCart.setOnClickListener(view -> finish());

        //banco de dados
        dbHelper = new MeuBancoHelper(this);

        //calendario
        etDate.setOnClickListener(v -> {
            Calendar calendario = Calendar.getInstance();
            int ano = calendario.get(Calendar.YEAR);
            int mes = calendario.get(Calendar.MONTH);
            int dia = calendario.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        // Formata a data como DD/MM/YYYY
                        String dataSelecionada = String.format("%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year);
                        etDate.setText(dataSelecionada);
                    }, ano, mes, dia);

            datePickerDialog.show();
        });


    }//oncreate

    private void salvarLista(){
        TextView etListTitle = findViewById(R.id.etListTitle);
        TextView etDate = findViewById(R.id.etDate);

        String titulo = etListTitle.getText().toString().trim();
        String data = etDate.getText().toString().trim();

        if (titulo.isEmpty() || data.isEmpty()) {
            Toast.makeText(this, "Preencha o título e a data!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (itens.isEmpty()) {
            Toast.makeText(this, "Adicione ao menos um item na lista!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Salvar no banco a lista (crie tabela para listas).
        // Exemplo: insira em "listas" (titulo, data)
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues valoresLista = new ContentValues();
        valoresLista.put("titulo", titulo);
        valoresLista.put("data", data);
        long listaId = db.insert("listas", null, valoresLista);

        // Salvar cada item vinculando pelo id da lista
        for (ItemLista item : itens) {
            ContentValues valores = new ContentValues();
            valores.put("lista_id", listaId);
            valores.put("descricao", item.nome);
            valores.put("quantidade", item.quantidade);
            valores.put("unidade", item.unidade);
            valores.put("categoria", item.categoria);
            db.insert("itens", null, valores);
        }

        Toast.makeText(this, "Lista salva com sucesso!", Toast.LENGTH_SHORT).show();
        finish();

    }

    private void abrirAdicionarItem() {
        abrirAdicionarItem(null, -1);
    }


    public void abrirAdicionarItem(ItemLista item, int position) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_item, null);

        ImageView ivMenos = dialogView.findViewById(R.id.ivMenos);
        ImageView ivMais = dialogView.findViewById(R.id.ivMais);
        TextView etDescricao = dialogView.findViewById(R.id.etDescricao);
        TextView etQuantidade = dialogView.findViewById(R.id.etQuantidade);
        Spinner spUnidade = dialogView.findViewById(R.id.spUnidade);
        Spinner spCategoria = dialogView.findViewById(R.id.spCategoria);

        String[] unidades = {"un", "dz", "ml", "L", "kg", "g", "Caixa", "Embalagem", "Galão", "Garrafa", "Lata", "Pacote"};
        String[] categorias = {
                "Bebidas", "Carnes", "Comidas Prontas e Congeladas",
                "Farmácia", "Frios, Leites e Derivados", "Frutas, ovos e verduras",
                "Higiene Pessoal", "Importados", "Limpeza", "Mercearia",
                "Padaria e Sobremesas", "Saúde e Beleza", "Sem Categoria", "Temperos"
        };
        ArrayAdapter<String> unidadeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, unidades);
        unidadeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUnidade.setAdapter(unidadeAdapter);

        ArrayAdapter<String> categoriaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categorias);
        categoriaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategoria.setAdapter(categoriaAdapter);

        // Preencher para editar
        if (item != null) {
            etDescricao.setText(item.nome);
            etQuantidade.setText(String.valueOf(item.quantidade));
            spUnidade.setSelection(unidadeAdapter.getPosition(item.unidade));
            spCategoria.setSelection(categoriaAdapter.getPosition(item.categoria));
        } else {
            etDescricao.setText("");
            etQuantidade.setText("1");
            spUnidade.setSelection(0);
            spCategoria.setSelection(0);
        }

        ivMenos.setOnClickListener(v -> {
            int valorAtual = Integer.parseInt(etQuantidade.getText().toString());
            if (valorAtual > 1) {
                etQuantidade.setText(String.valueOf(valorAtual - 1));
            }
        });

        ivMais.setOnClickListener(v -> {
            int valorAtual = Integer.parseInt(etQuantidade.getText().toString());
            etQuantidade.setText(String.valueOf(valorAtual + 1));
        });

        Button btnSalvarDialog = dialogView.findViewById(R.id.btnSalvarDialog);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        btnSalvarDialog.setOnClickListener(v -> {
            String descricao = etDescricao.getText().toString().trim();
            String quantidadeStr = etQuantidade.getText().toString().trim();
            String unidade = spUnidade.getSelectedItem().toString();
            String categoria = spCategoria.getSelectedItem().toString();

            if (descricao.isEmpty() || quantidadeStr.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
                return;
            }

            int quantidade = Integer.parseInt(quantidadeStr);

            if (item == null) {
                // Adiciona novo
                ItemLista novo = new ItemLista(descricao, categoria, unidade, quantidade);
                itens.add(novo);
                adapter.notifyItemInserted(itens.size() - 1);
            } else {
                // Edita
                item.nome = descricao;
                item.quantidade = quantidade;
                item.unidade = unidade;
                item.categoria = categoria;
                adapter.notifyItemChanged(position);
            }

            dialog.dismiss();
        });

        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.95),
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }




}//fim da class
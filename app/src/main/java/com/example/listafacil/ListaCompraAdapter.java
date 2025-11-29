package com.example.listafacil;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ListaCompraAdapter extends RecyclerView.Adapter<ListaCompraAdapter.ListaViewHolder> {
    private Context context;
    private List<ListaCompra> listas;

    public ListaCompraAdapter(Context context, List<ListaCompra> listas) {
        this.context = context;
        this.listas = listas;
    }

    @NonNull
    @Override
    public ListaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_shopping_list, parent, false);
        return new ListaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaViewHolder holder, int position) {
        ListaCompra lista = listas.get(position);
        holder.tvListName.setText(lista.titulo);
        holder.tvCreatedDate.setText("Criado " + lista.data);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ListaSelecionada.class);
            intent.putExtra("listaId", lista.id);
            intent.putExtra("titulo", lista.titulo);
            intent.putExtra("data", lista.data);
            intent.putExtra("itens", new ArrayList<>(lista.itens));
            context.startActivity(intent);
        });

        holder.ivDropdown.setOnClickListener(v -> {
            if (holder.llExpandedItems.getVisibility() == View.GONE) {
                holder.llExpandedItems.setVisibility(View.VISIBLE);
                holder.ivDropdown.setRotation(180);
                holder.llExpandedItems.removeAllViews();
                for (ItemLista item : lista.itens) {
                    TextView tvItem = new TextView(context);
                    tvItem.setText("• " + item.nome);
                    tvItem.setTextSize(14);
                    tvItem.setTextColor(context.getResources().getColor(R.color.text_white));
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 0, 0, (int) (4 * context.getResources().getDisplayMetrics().density));
                    tvItem.setLayoutParams(params);
                    holder.llExpandedItems.addView(tvItem);
                }
            } else {
                holder.llExpandedItems.setVisibility(View.GONE);
                holder.ivDropdown.setRotation(0);
            }
        });

        holder.ivEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, AdicionarLista.class);
            intent.putExtra("listaId", (long) lista.id);
            intent.putExtra("titulo", lista.titulo);
            intent.putExtra("data", lista.data);
            intent.putExtra("itens", new ArrayList<>(lista.itens)); // ItemLista deve implementar Serializable
            context.startActivity(intent);
        });


        holder.ivDelete.setOnClickListener(v -> {
            MeuBancoHelper dbHelper = new MeuBancoHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            // Excluir todos os itens associados
            db.delete("itens", "lista_id = ?", new String[]{String.valueOf(lista.id)});
            // Excluir a lista
            db.delete("listas", "id = ?", new String[]{String.valueOf(lista.id)});

            db.close();

            // Atualizar lista local e RecyclerView
            listas.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, listas.size());
        });
    }


    @Override
    public int getItemCount() {
        return listas.size();
    }

    static class ListaViewHolder extends RecyclerView.ViewHolder {
        TextView tvListName, tvCreatedDate;
        ImageView ivDropdown, ivEdit, ivDelete;
        LinearLayout llExpandedItems;

        public ListaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvListName = itemView.findViewById(R.id.tvListName);
            tvCreatedDate = itemView.findViewById(R.id.tvCreatedDate);
            ivDropdown = itemView.findViewById(R.id.ivDropdown);
            ivEdit = itemView.findViewById(R.id.ivEdit);
            ivDelete = itemView.findViewById(R.id.ivDelete);
            llExpandedItems = itemView.findViewById(R.id.llExpandedItems);
        }
    }
}

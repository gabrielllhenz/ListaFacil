package com.example.listafacil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HistoricoAdapter extends RecyclerView.Adapter<HistoricoAdapter.HistoricoViewHolder> {
    private List<HistoricoCompra> historicos;
    private Context context;

    public HistoricoAdapter(Context context, List<HistoricoCompra> historicos) {
        this.context = context;
        this.historicos = historicos;
    }

    @NonNull
    @Override
    public HistoricoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_historico_card, parent, false);
        return new HistoricoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoricoViewHolder holder, int position) {
        HistoricoCompra historico = historicos.get(position);

        holder.tvListName.setText(historico.nomeLista);
        holder.tvPurchaseDate.setText(historico.dataCompra);
        holder.tvTotal.setText(String.format("R$ %.2f", historico.total));

        // Limpar itens anteriores
        holder.llItems.removeAllViews();

        // Adicionar itens
        for (HistoricoCompra.ItemHistorico item : historico.itens) {
            LinearLayout itemRow = new LinearLayout(context);
            itemRow.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            rowParams.setMargins(0, 0, 0, (int) (8 * context.getResources().getDisplayMetrics().density));
            itemRow.setLayoutParams(rowParams);

            // Nome e quantidade
            TextView tvNome = new TextView(context);
            tvNome.setText(item.descricao + " (" + item.quantidade + " " + item.unidade + ")");
            tvNome.setTextSize(14);
            tvNome.setTextColor(context.getResources().getColor(R.color.text_white));
            LinearLayout.LayoutParams nomeParams = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1.0f
            );
            tvNome.setLayoutParams(nomeParams);

            // Preço
            TextView tvPreco = new TextView(context);
            tvPreco.setText(String.format("R$ %.2f", item.preco * item.quantidade));
            tvPreco.setTextSize(14);
            tvPreco.setTextColor(context.getResources().getColor(R.color.text_white));
            tvPreco.setGravity(android.view.Gravity.END);

            itemRow.addView(tvNome);
            itemRow.addView(tvPreco);
            holder.llItems.addView(itemRow);
        }
    }

    @Override
    public int getItemCount() {
        return historicos.size();
    }

    public static class HistoricoViewHolder extends RecyclerView.ViewHolder {
        TextView tvListName, tvPurchaseDate, tvTotal;
        LinearLayout llItems;

        public HistoricoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvListName = itemView.findViewById(R.id.tvListName);
            tvPurchaseDate = itemView.findViewById(R.id.tvPurchaseDate);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            llItems = itemView.findViewById(R.id.llItems);
        }
    }
}

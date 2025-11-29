package com.example.listafacil;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ItemCheckAdapter extends RecyclerView.Adapter<ItemCheckAdapter.ItemCheckViewHolder> {
    private List<ItemLista> itens;
    private Context context;
    private OnItemChangedListener listener;

    public interface OnItemChangedListener {
        void onItemChanged();
    }

    public void setOnItemChangedListener(OnItemChangedListener listener) {
        this.listener = listener;
    }

    public ItemCheckAdapter(Context context, List<ItemLista> itens) {
        this.context = context;
        this.itens = itens;
    }

    @NonNull
    @Override
    public ItemCheckViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_check, parent, false);
        return new ItemCheckViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemCheckViewHolder holder, int position) {
        ItemLista item = itens.get(position);

        holder.tvItemName.setText(item.nome);
        holder.tvCategory.setText("• " + item.categoria + " •");
        holder.tvQuantity.setText(String.valueOf(item.quantidade));
        holder.tvUnit.setText(item.unidade);

        // Checkbox
        holder.cbComprado.setOnCheckedChangeListener(null); // Remove listener antigo
        holder.cbComprado.setChecked(item.comprado);
        holder.cbComprado.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.comprado = isChecked;
            if (listener != null) listener.onItemChanged();
        });

        // Preço com máscara R$
        holder.etPreco.removeTextChangedListener(holder.textWatcher); // Remove listener antigo
        if (item.preco > 0) {
            holder.etPreco.setText("R$ " + String.format("%.2f", item.preco));
        } else {
            holder.etPreco.setText("");
        }

        holder.textWatcher = new TextWatcher() {
            private boolean isUpdating = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isUpdating) return;

                isUpdating = true;
                String str = s.toString().replace("R$", "").replace(",", ".").trim();

                try {
                    if (!str.isEmpty()) {
                        item.preco = Double.parseDouble(str);
                        if (!s.toString().startsWith("R$ ")) {
                            holder.etPreco.setText("R$ " + str);
                            holder.etPreco.setSelection(holder.etPreco.getText().length());
                        }
                    } else {
                        item.preco = 0;
                    }
                } catch (NumberFormatException e) {
                    item.preco = 0;
                }

                if (listener != null) listener.onItemChanged();
                isUpdating = false;
            }
        };
        holder.etPreco.addTextChangedListener(holder.textWatcher);

        // Controle de quantidade
        holder.ivMinus.setOnClickListener(v -> {
            if (item.quantidade > 1) {
                item.quantidade--;
                holder.tvQuantity.setText(String.valueOf(item.quantidade));
                if (listener != null) listener.onItemChanged();
            }
        });

        holder.ivPlus.setOnClickListener(v -> {
            item.quantidade++;
            holder.tvQuantity.setText(String.valueOf(item.quantidade));
            if (listener != null) listener.onItemChanged();
        });
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }

    public static class ItemCheckViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvCategory, tvQuantity, tvUnit;
        ImageView ivMinus, ivPlus;
        CheckBox cbComprado;
        EditText etPreco;
        TextWatcher textWatcher;

        public ItemCheckViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvUnit = itemView.findViewById(R.id.tvUnit);
            ivMinus = itemView.findViewById(R.id.ivMinus);
            ivPlus = itemView.findViewById(R.id.ivPlus);
            cbComprado = itemView.findViewById(R.id.cbComprado);
            etPreco = itemView.findViewById(R.id.etPreco);
        }
    }
}

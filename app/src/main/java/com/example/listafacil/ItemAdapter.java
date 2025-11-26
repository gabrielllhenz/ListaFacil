package com.example.listafacil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private List<ItemLista> itens;
    private Context context;
    private OnEditClickListener editClickListener;

    // Interface para click de edição
    public interface OnEditClickListener {
        void onEdit(ItemLista item, int position);
    }

    public void setOnEditClickListener(OnEditClickListener listener) {
        this.editClickListener = listener;
    }

    public ItemAdapter(Context context, List<ItemLista> itens) {
        this.context = context;
        this.itens = itens;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_row, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        ItemLista item = itens.get(position);
        holder.tvItemName.setText(item.nome);
        holder.tvCategory.setText("• " + item.categoria + " •");
        holder.tvQuantity.setText(String.valueOf(item.quantidade));
        holder.tvUnit.setText(item.unidade);

        holder.ivMinus.setOnClickListener(v -> {
            if (item.quantidade > 1) {
                item.quantidade--;
                holder.tvQuantity.setText(String.valueOf(item.quantidade));
            }
        });

        holder.ivPlus.setOnClickListener(v -> {
            item.quantidade++;
            holder.tvQuantity.setText(String.valueOf(item.quantidade));
        });

        holder.ivDelete.setOnClickListener(v -> {
            itens.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, itens.size());
        });

        holder.ivEdit.setOnClickListener(v -> {
            if (editClickListener != null) {
                editClickListener.onEdit(item, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvCategory, tvQuantity, tvUnit;
        ImageView ivMinus, ivPlus, ivEdit, ivDelete;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvUnit = itemView.findViewById(R.id.tvUnit);
            ivMinus = itemView.findViewById(R.id.ivMinus);
            ivPlus = itemView.findViewById(R.id.ivPlus);
            ivEdit = itemView.findViewById(R.id.ivEdit);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }
}

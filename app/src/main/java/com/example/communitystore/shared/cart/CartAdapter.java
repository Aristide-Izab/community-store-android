package com.example.communitystore.shared.cart;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.communitystore.R;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    public interface OnCartChangedListener {
        void onCartChanged();
    }

    private final List<CartItem> items;
    private final OnCartChangedListener listener;
    private final int themeColor;
    private boolean editMode = false;

    public CartAdapter(List<CartItem> items, int themeColor, OnCartChangedListener listener) {
        this.items = items;
        this.themeColor = themeColor;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart_product, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = items.get(position);

        holder.title.setText(item.getTitle());
        holder.price.setText(formatPrice(item.getUnitPrice()));
        holder.price.setTextColor(themeColor);
        holder.quantity.setText(String.valueOf(item.getQuantity()));

        if (item.getImageResource() != 0) {
            holder.image.setImageResource(item.getImageResource());
        } else {
            holder.image.setImageResource(R.drawable.dell_laptop_1);
        }

        holder.dragHandle.setVisibility(editMode ? View.VISIBLE : View.GONE);
        holder.deleteButton.setVisibility(View.VISIBLE);

        holder.plusButton.setOnClickListener(v -> {
            int adapterPosition = holder.getBindingAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) return;

            CartItem currentItem = items.get(adapterPosition);
            currentItem.setQuantity(currentItem.getQuantity() + 1);
            notifyItemChanged(adapterPosition);
            listener.onCartChanged();
        });

        holder.minusButton.setOnClickListener(v -> {
            int adapterPosition = holder.getBindingAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) return;

            CartItem currentItem = items.get(adapterPosition);
            if (currentItem.getQuantity() > 1) {
                currentItem.setQuantity(currentItem.getQuantity() - 1);
                notifyItemChanged(adapterPosition);
                listener.onCartChanged();
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            int adapterPosition = holder.getBindingAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) return;

            items.remove(adapterPosition);
            notifyItemRemoved(adapterPosition);
            listener.onCartChanged();
        });

        ViewCompat.setBackgroundTintList(
                holder.quantityContainer,
                ColorStateList.valueOf(0xFFF3F4F6)
        );
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
        notifyDataSetChanged();
    }

    public boolean isEditMode() {
        return editMode;
    }

    public boolean moveItem(int fromPosition, int toPosition) {
        if (!editMode || fromPosition < 0 || toPosition < 0
                || fromPosition >= items.size() || toPosition >= items.size()) {
            return false;
        }

        Collections.swap(items, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        listener.onCartChanged();
        return true;
    }

    public List<CartItem> getItems() {
        return items;
    }

    private String formatPrice(int amount) {
        NumberFormat formatter = NumberFormat.getIntegerInstance(Locale.US);
        return "R" + formatter.format(amount);
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        ImageView deleteButton;
        ImageView dragHandle;
        TextView title;
        TextView price;
        TextView quantity;
        TextView plusButton;
        TextView minusButton;
        View quantityContainer;

        CartViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.cartItemImage);
            deleteButton = itemView.findViewById(R.id.cartItemDelete);
            dragHandle = itemView.findViewById(R.id.cartDragHandle);
            title = itemView.findViewById(R.id.cartItemTitle);
            price = itemView.findViewById(R.id.cartItemPrice);
            quantity = itemView.findViewById(R.id.cartItemQuantity);
            plusButton = itemView.findViewById(R.id.cartItemPlus);
            minusButton = itemView.findViewById(R.id.cartItemMinus);
            quantityContainer = itemView.findViewById(R.id.cartQuantityContainer);
        }
    }
}

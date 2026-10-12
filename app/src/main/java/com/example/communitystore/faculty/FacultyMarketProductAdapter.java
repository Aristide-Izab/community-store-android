package com.example.communitystore.faculty;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.communitystore.R;
import com.example.communitystore.shared.utils.ThemeUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class FacultyMarketProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface Listener {
        void onProductClick(FacultyMarketProduct product);

        void onMyOrdersClick();
    }

    private final List<FacultyMarketRow> rows = new ArrayList<>();
    private final Set<String> favourites = new HashSet<>();
    private final Listener listener;

    public FacultyMarketProductAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submit(List<FacultyMarketRow> newRows) {
        rows.clear();
        rows.addAll(newRows);
        notifyDataSetChanged();
    }

    public int getSpanSize(int position, int totalSpan) {
        return rows.get(position).type == FacultyMarketRow.TYPE_PRODUCT ? 1 : totalSpan;
    }

    @Override
    public int getItemViewType(int position) {
        return rows.get(position).type;
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case FacultyMarketRow.TYPE_PROMO:
                return new PromoHolder(inflater.inflate(R.layout.faculty_item_market_promo, parent, false));
            case FacultyMarketRow.TYPE_HEADER:
                return new HeaderHolder(inflater.inflate(R.layout.faculty_item_market_header, parent, false));
            default:
                return new ProductHolder(inflater.inflate(R.layout.faculty_item_market_product, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FacultyMarketRow row = rows.get(position);

        if (holder instanceof PromoHolder) {
            ((PromoHolder) holder).bind();
        } else if (holder instanceof HeaderHolder) {
            ((HeaderHolder) holder).title.setText(row.title);
        } else {
            ((ProductHolder) holder).bind(row.product);
        }
    }

    private class PromoHolder extends RecyclerView.ViewHolder {
        private final View ordersButton;

        PromoHolder(View view) {
            super(view);
            ordersButton = view.findViewById(R.id.myOrdersButton);
        }

        void bind() {
            ordersButton.setOnClickListener(v -> listener.onMyOrdersClick());
        }
    }

    private static class HeaderHolder extends RecyclerView.ViewHolder {
        private final TextView title;

        HeaderHolder(View view) {
            super(view);
            title = view.findViewById(R.id.sectionTitle);
        }
    }

    private class ProductHolder extends RecyclerView.ViewHolder {
        private final ImageView image;
        private final ImageView favourite;
        private final TextView title;
        private final TextView price;
        private final TextView rating;
        private final TextView verified;

        ProductHolder(View view) {
            super(view);
            image = view.findViewById(R.id.productImage);
            favourite = view.findViewById(R.id.productFavourite);
            title = view.findViewById(R.id.productTitle);
            price = view.findViewById(R.id.productPrice);
            rating = view.findViewById(R.id.productRating);
            verified = view.findViewById(R.id.productVerified);
        }

        void bind(FacultyMarketProduct product) {
            Context context = itemView.getContext();

            image.setImageResource(product.images[0]);
            title.setText(product.title);
            price.setText(
                    product.hasFixedPrice()
                            ? String.format(Locale.US, "R%,.0f", product.price)
                            : "Message for price"
            );
            rating.setText(String.format(Locale.US, "%.1f", product.rating));
            verified.setTextColor(ThemeUtils.primary(context));

            setFavouriteStyle(favourite, favourites.contains(product.id));

            favourite.setOnClickListener(v -> {
                if (!favourites.remove(product.id)) {
                    favourites.add(product.id);
                }
                setFavouriteStyle(favourite, favourites.contains(product.id));
            });

            itemView.setOnClickListener(v -> listener.onProductClick(product));
        }
    }

    private void setFavouriteStyle(ImageView heart, boolean liked) {
        ViewCompat.setBackgroundTintList(
                heart,
                ColorStateList.valueOf(liked ? Color.parseColor("#E53935") : Color.WHITE)
        );
        heart.setColorFilter(liked ? Color.WHITE : Color.parseColor("#111111"));
    }
}

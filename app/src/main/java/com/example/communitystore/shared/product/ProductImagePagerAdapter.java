package com.example.communitystore.shared.product;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.communitystore.R;

public class ProductImagePagerAdapter extends RecyclerView.Adapter<ProductImagePagerAdapter.ImageViewHolder> {

    private final int[] imageResources;

    public ProductImagePagerAdapter(int[] imageResources) {
        this.imageResources = imageResources;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_image_page, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        holder.image.setImageResource(imageResources[position]);
    }

    @Override
    public int getItemCount() {
        return imageResources.length;
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        final ImageView image;

        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.productPageImage);
        }
    }
}

package com.scan.pay.shop.scancart.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.scan.pay.shop.scancart.Activities.CartActivity;
import com.scan.pay.shop.scancart.DataDeclaration;
import com.scan.pay.shop.scancart.Models.ProductModel;
import com.scan.pay.shop.scancart.R;

import java.util.Objects;

public class ProductListAdapter extends ListAdapter<ProductModel, ProductListAdapter.ViewHolder> {

    private final Context context;

    public ProductListAdapter(Context context, @NonNull DiffUtil.ItemCallback<ProductModel> diffCallback) {
        super(diffCallback);
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return ViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductModel productModel = getItem(position);
        holder.bind(productModel);
        holder.linearLayoutProduct.setOnClickListener(view -> new Thread(() -> {
            Intent nextActivity = new Intent(context, CartActivity.class);
            nextActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            nextActivity.putExtra(DataDeclaration.QR_TAG, productModel.getProductQRTag());
            context.startActivity(nextActivity);
        }).start());
    }

    public static class ProductDiff extends DiffUtil.ItemCallback<ProductModel> {

        @Override
        public boolean areItemsTheSame(@NonNull ProductModel oldItem, @NonNull ProductModel newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull ProductModel oldItem, @NonNull ProductModel newItem) {
            return Objects.equals(oldItem, newItem);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView productImage;
        public TextView textProductTitle;
        public TextView textProductCost;
        public LinearLayout linearLayoutProduct;

        public ViewHolder(View itemView) {
            super(itemView);
            this.linearLayoutProduct = itemView.findViewById(R.id.layout_product_list);
            this.productImage = itemView.findViewById(R.id.image_product_image);
            this.textProductTitle = itemView.findViewById(R.id.text_product_name);
            this.textProductCost = itemView.findViewById(R.id.text_product_cost);
        }

        public static ViewHolder create(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.product_list_item, parent, false);
            return new ViewHolder(view);
        }

        public void bind(ProductModel productModel) {
            Log.e("Adapter: ", "Data: " + productModel.getProductName());
            this.textProductTitle.setText(productModel.getProductName());
            this.textProductCost.setText("$ " + productModel.getProductPrice());
            try {
                this.productImage.setImageBitmap(BitmapFactory.decodeFile(productModel.getProductImageUri()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

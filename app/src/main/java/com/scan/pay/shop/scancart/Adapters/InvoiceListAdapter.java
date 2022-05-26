package com.scan.pay.shop.scancart.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.scan.pay.shop.scancart.Models.ProductModel;
import com.scan.pay.shop.scancart.R;

import java.util.List;

public class InvoiceListAdapter extends RecyclerView.Adapter<InvoiceListAdapter.ViewHolder> {

    private final List<ProductModel> mProducts;
    private final LayoutInflater mInflater;

    public InvoiceListAdapter(Context context, List<ProductModel> productModels) {
        this.mInflater = LayoutInflater.from(context);
        this.mProducts = productModels;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.invoice_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductModel productModel = mProducts.get(position);
        holder.textProductTitle.setText(productModel.getProductName());
        holder.textProductCost.setText("\u20B9 " + productModel.getProductPrice());
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mProducts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textProductTitle;
        public TextView textProductCost;

        public ViewHolder(View itemView) {
            super(itemView);
            this.textProductTitle = itemView.findViewById(R.id.text_invoice_product_name);
            this.textProductCost = itemView.findViewById(R.id.text_invoice_product_cost);
        }
    }
}

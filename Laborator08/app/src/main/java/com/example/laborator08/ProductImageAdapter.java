package com.example.laborator08;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

public class ProductImageAdapter extends BaseAdapter {
    private Context context;
    private List<ProductImageInfo> items;

    public ProductImageAdapter(Context context, List<ProductImageInfo> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() { return items.size(); }

    @Override
    public Object getItem(int position) { return items.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_product_image, parent, false);
        }

        ProductImageInfo item = items.get(position);
        ImageView ivProduct = convertView.findViewById(R.id.iv_product);
        TextView tvDescription = convertView.findViewById(R.id.tv_description);

        tvDescription.setText(item.getDescription());
        if (item.getImage() != null) {
            ivProduct.setImageBitmap(item.getImage());
        } else {
            ivProduct.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        return convertView;
    }
}

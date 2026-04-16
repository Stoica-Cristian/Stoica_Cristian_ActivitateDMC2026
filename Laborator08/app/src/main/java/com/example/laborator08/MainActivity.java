package com.example.laborator08;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText etName, etPrice, etQuantity;
    private EditText etSearchName, etMinQ, etMaxQ, etPriceThreshold, etPrefix;
    private Button btnInsert, btnGetAll, btnGetByName, btnGetRange, btnDeletePrice, btnUpdatePrefix, btnOpenImages;
    private ListView lvProducts;

    private AppDatabase db;
    private ArrayAdapter<Product> adapter;
    private List<Product> productList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = AppDatabase.getInstance(this);

        initViews();
        setupListView();
        setupListeners();
    }

    private void initViews() {
        etName = findViewById(R.id.et_name);
        etPrice = findViewById(R.id.et_price);
        etQuantity = findViewById(R.id.et_quantity);
        etSearchName = findViewById(R.id.et_search_name);
        etMinQ = findViewById(R.id.et_min_q);
        etMaxQ = findViewById(R.id.et_max_q);
        etPriceThreshold = findViewById(R.id.et_price_threshold);
        etPrefix = findViewById(R.id.et_prefix);

        btnInsert = findViewById(R.id.btn_insert);
        btnGetAll = findViewById(R.id.btn_get_all);
        btnGetByName = findViewById(R.id.btn_get_by_name);
        btnGetRange = findViewById(R.id.btn_get_range);
        btnDeletePrice = findViewById(R.id.btn_delete_price);
        btnUpdatePrefix = findViewById(R.id.btn_update_prefix);
        btnOpenImages = findViewById(R.id.btn_open_images);

        lvProducts = findViewById(R.id.lv_products);
    }

    private void setupListView() {
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, productList);
        lvProducts.setAdapter(adapter);
    }

    private void setupListeners() {
        btnOpenImages.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ImagesActivity.class);
            startActivity(intent);
        });

        // 1. Inserare
        btnInsert.setOnClickListener(v -> {
            String name = etName.getText().toString();
            String priceStr = etPrice.getText().toString();
            String qStr = etQuantity.getText().toString();

            if (!name.isEmpty() && !priceStr.isEmpty() && !qStr.isEmpty()) {
                Product p = new Product(name, Double.parseDouble(priceStr), Integer.parseInt(qStr));
                db.productDao().insert(p);
                Toast.makeText(this, "Produs inserat", Toast.LENGTH_SHORT).show();
                refreshList();
            }
        });

        // 2. Selectie toate
        btnGetAll.setOnClickListener(v -> refreshList());

        // 3. Selectie dupa nume
        btnGetByName.setOnClickListener(v -> {
            String name = etSearchName.getText().toString();
            if (!name.isEmpty()) {
                Product p = db.productDao().getByName(name);
                productList.clear();
                if (p != null) productList.add(p);
                adapter.notifyDataSetChanged();
            }
        });

        // 4. Selectie interval cantitate
        btnGetRange.setOnClickListener(v -> {
            String minStr = etMinQ.getText().toString();
            String maxStr = etMaxQ.getText().toString();
            if (!minStr.isEmpty() && !maxStr.isEmpty()) {
                List<Product> results = db.productDao().getByQuantityRange(Integer.parseInt(minStr), Integer.parseInt(maxStr));
                productList.clear();
                productList.addAll(results);
                adapter.notifyDataSetChanged();
            }
        });

        // 5. Stergere dupa prag pret
        btnDeletePrice.setOnClickListener(v -> {
            String thresholdStr = etPriceThreshold.getText().toString();
            if (!thresholdStr.isEmpty()) {
                db.productDao().deleteByPriceGreaterThan(Double.parseDouble(thresholdStr));
                refreshList();
            }
        });

        // 6. Update dupa prefix
        btnUpdatePrefix.setOnClickListener(v -> {
            String prefix = etPrefix.getText().toString();
            if (!prefix.isEmpty()) {
                db.productDao().incrementQuantityByNamePrefix(prefix);
                refreshList();
            }
        });
    }

    private void refreshList() {
        List<Product> all = db.productDao().getAll();
        productList.clear();
        productList.addAll(all);
        adapter.notifyDataSetChanged();
    }
}

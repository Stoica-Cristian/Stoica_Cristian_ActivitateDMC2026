package com.example.laborator08;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface ProductDao {
    @Insert
    void insert(Product product);

    @Query("SELECT * FROM products")
    List<Product> getAll();

    @Query("SELECT * FROM products WHERE name = :name")
    Product getByName(String name);

    @Query("SELECT * FROM products WHERE quantity BETWEEN :min AND :max")
    List<Product> getByQuantityRange(int min, int max);

    @Query("DELETE FROM products WHERE price > :threshold")
    void deleteByPriceGreaterThan(double threshold);

    @Query("UPDATE products SET quantity = quantity + 1 WHERE name LIKE :prefix || '%'")
    void incrementQuantityByNamePrefix(String prefix);
}

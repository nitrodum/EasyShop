package org.yearup.data;

import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;

public interface ShoppingCartDao
{
    ShoppingCart getByUserId(int userId);
    // add additional method signatures here
    void addProduct(int userId, Product product);
    void update(int userId, int productId);
    void delete(int userId, int productId);
}

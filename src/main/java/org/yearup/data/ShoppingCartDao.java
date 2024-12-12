package org.yearup.data;

import org.yearup.models.ShoppingCart;

public interface ShoppingCartDao
{
    ShoppingCart getByUserId(int userId);
    // add additional method signatures here
    ShoppingCart addProduct(int userId, int productId);
    void update(int userId, int productId);
    void delete(int userId, int productId);
}

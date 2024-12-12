package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao {

    ProductDao productDao;

    public MySqlShoppingCartDao(DataSource dataSource, ProductDao productDao) {
        super(dataSource);
        this.productDao = productDao;
    }

    @Override
    public ShoppingCart getByUserId(int userId) {
        ShoppingCart cart = new ShoppingCart();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     SELECT * FROM shopping_cart
                     WHERE user_id = ?""")
        ) {
            statement.setInt(1, userId);

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Product product = productDao.getById(rs.getInt("product_id"));
                int quantity = rs.getInt("quantity");
                ShoppingCartItem item = new ShoppingCartItem();
                item.setProduct(product);
                item.setQuantity(quantity);
                cart.add(item);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return cart;
    }

    @Override
    public ShoppingCart addProduct(int userId, int productId) {
        return null;
    }

    @Override
    public void update(int userId, int productId) {

    }

    @Override
    public void delete(int userId, int productId) {

    }
}

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

    private ProductDao productDao;

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
    public void addProduct(int userId, Product product) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     INSERT INTO shopping_cart(user_id, product_id, quantity)
                     VALUES (?, ?, 1)""", PreparedStatement.RETURN_GENERATED_KEYS);
        ) {
            statement.setInt(1, userId);
            statement.setInt(2, product.getProductId());

            int row = statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void update(int userId, ShoppingCartItem item) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     UPDATE shopping_cart
                     SET quantity = ?
                     WHERE user_id = ? AND product_id = ?""")
        ) {
            statement.setInt(1, item.getQuantity() + 1);
            statement.setInt(2, userId);
            statement.setInt(3, item.getProductId());

            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int userId) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     DELETE FROM shopping_cart
                     WHERE user_id = ?""")
        ) {
            statement.setInt(1, userId);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete shopping cart for user ID: " + userId, e);
        }
    }
}

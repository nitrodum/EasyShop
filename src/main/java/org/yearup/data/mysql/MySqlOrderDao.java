package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.OrderDao;
import org.yearup.data.OrderItemDao;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Order;
import org.yearup.models.OrderItem;
import org.yearup.models.Product;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;

@Component
public class MySqlOrderDao extends MySqlDaoBase implements OrderDao {

    private final ProductDao productDao;
    private final OrderItemDao orderItemDao;
    private final ShoppingCartDao shoppingCartDao;

    public MySqlOrderDao(DataSource dataSource, ProductDao productDao, OrderItemDao orderItemDao, MySqlShoppingCartDao shoppingCartDao) {
        super(dataSource);
        this.productDao = productDao;
        this.orderItemDao = orderItemDao;
        this.shoppingCartDao = shoppingCartDao;
    }

    @Override
    public Order create(int userId) {
        Order order = new Order();
        order.setUserId(userId);
        order.setDate(LocalDateTime.now());

        setOrderAddress(userId, order);
        order.setOrderId(setUserDetails(userId, order));

        double shippingAmount = 0;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     SELECT * FROM shopping_cart
                     WHERE user_id = ?""")
        ) {
            statement.setInt(1, userId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    int productId = rs.getInt("product_id");
                    Product product = productDao.getById(productId);
                    int quantity = rs.getInt("quantity");
                    OrderItem item = new OrderItem();
                    item.setOrderId(order.getOrderId());
                    item.setProductId(productId);
                    item.setQuantity(quantity);
                    item.setSalesPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
                    shippingAmount += item.getSalesPrice().doubleValue();
                    orderItemDao.create(item);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        order.setShipping_amount(BigDecimal.valueOf(shippingAmount));

        updateShippingAmount(order);

        shoppingCartDao.delete(userId);

        return order;
    }

    private void updateShippingAmount(Order order) {
        try (Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement("""
                    UPDATE orders
                    SET shipping_amount = ?
                    WHERE order_id = ?""")
        ) {
            statement.setBigDecimal(1, order.getShipping_amount());
            statement.setInt(2, order.getOrderId());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private int setUserDetails(int userId, Order order) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO orders(user_id, date, address, city, state, zip)
                    VALUES(?, ?, ?, ?, ?, ?)""", PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            statement.setInt(1, userId);
            statement.setTimestamp(2, Timestamp.valueOf(order.getDate()));
            statement.setString(3, order.getAddress());
            statement.setString(4, order.getCity());
            statement.setString(5, order.getState());
            statement.setString(6, order.getZip());

            int rows = statement.executeUpdate();

            if (rows > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();

                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }

    private void setOrderAddress(int userId, Order order) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                    SELECT * FROM profiles
                    WHERE user_id = ?""")
        ) {
            statement.setInt(1, userId);

            ResultSet row = statement.executeQuery();

            if (row.next()) {
                order.setAddress(row.getString("Address"));
                order.setCity(row.getString("City"));
                order.setState(row.getString("State"));
                order.setZip(row.getString("Zip"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

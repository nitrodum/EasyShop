package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.models.Profile;
import org.yearup.data.ProfileDao;

import javax.sql.DataSource;
import java.sql.*;

@Component
public class MySqlProfileDao extends MySqlDaoBase implements ProfileDao
{
    public MySqlProfileDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public Profile getById(int userId) {
        try (Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM profiles WHERE user_id = ?")
        ) {
            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery())
            {
                if (resultSet.next())
                {
                    return new Profile(
                            resultSet.getInt("user_id"),
                            resultSet.getString("first_name"),
                            resultSet.getString("last_name"),
                            resultSet.getString("phone"),
                            resultSet.getString("email"),
                            resultSet.getString("address"),
                            resultSet.getString("city"),
                            resultSet.getString("state"),
                            resultSet.getString("zip")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Profile create(Profile profile)
    {
        String sql = "INSERT INTO profiles (user_id, first_name, last_name, phone, email, address, city, state, zip) " +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try(Connection connection = getConnection())
        {
            PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, profile.getUserId());
            ps.setString(2, profile.getFirstName());
            ps.setString(3, profile.getLastName());
            ps.setString(4, profile.getPhone());
            ps.setString(5, profile.getEmail());
            ps.setString(6, profile.getAddress());
            ps.setString(7, profile.getCity());
            ps.setString(8, profile.getState());
            ps.setString(9, profile.getZip());

            ps.executeUpdate();

            return profile;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(int userId, Profile profile) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     UPDATE profiles
                     SET first_name = ?,
                     last_name = ?,
                     phone = ?,
                     email = ?,
                     address = ?,
                     city = ?,
                     state = ?,
                     zip = ?
                     WHERE user_id = ?""")
        ) {
            statement.setString(1, profile.getFirstName());
            statement.setString(2, profile.getLastName());
            statement.setString(3, profile.getPhone());
            statement.setString(4, profile.getEmail());
            statement.setString(5, profile.getAddress());
            statement.setString(6, profile.getCity());
            statement.setString(7, profile.getState());
            statement.setString(8, profile.getZip());
            statement.setInt(9, userId);

            int rows = statement.executeUpdate();

            if (rows == 0) {
                throw new RuntimeException("No profiles found with user ID: " + userId);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}

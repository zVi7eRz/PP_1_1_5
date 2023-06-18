package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDaoJDBCImpl implements UserDao {
    private final Connection connection = Util.getConnection();

    private final String userTableName = "users";

    public UserDaoJDBCImpl() {

    }

    public void createUsersTable() {

        try (ResultSet resultSet = connection.createStatement().executeQuery(String.format("SHOW TABLES FROM test LIKE '%s'", userTableName))) {
            if (resultSet.next()) {
                System.out.println("Table exists");
            } else {
                connection.createStatement().executeUpdate(String.format("CREATE TABLE %s (id BIGINT NOT NULL AUTO_INCREMENT, name VARCHAR(255), "
                                                                        + "lastName VARCHAR(255), age TINYINT, PRIMARY KEY ( id ))", userTableName));
                System.out.println("Table create");
            }
        } catch (SQLException ignored) {
        }


    }

    public void dropUsersTable() {
        try {
            connection.createStatement().executeUpdate(String.format("DROP TABLES %s", userTableName));
        } catch (SQLException ignored) {
        }
    }

    public void saveUser(String name, String lastName, byte age) {
        try (PreparedStatement prepared = connection.prepareStatement(String.format("INSERT INTO %s (name, lastName, age) VALUES (?, ?, ?)", userTableName))) {
            prepared.setString(1, name);
            prepared.setString(2, lastName);
            prepared.setByte(3, age);
            prepared.execute();
            System.out.printf("User с именем – %s добавлен в базу данных\n", name);
        } catch (SQLException ignored) {
        }

    }

    public void removeUserById(long id) {
        try (PreparedStatement prepared = connection.prepareStatement(String.format("DELETE FROM %s WHERE id = ?", userTableName))) {
            prepared.setLong(1, id);
            prepared.executeUpdate();
            System.out.printf("User %s deleted \n", id);
        } catch (SQLException ignored) {
        }
    }

    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        try (ResultSet rs = connection.createStatement().executeQuery(String.format("SELECT * FROM %s", userTableName))) {

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setName(rs.getString("name"));
                user.setLastName(rs.getString("lastName"));
                user.setAge(rs.getByte("age"));
                userList.add(user);
            }
            return userList;
        } catch (SQLException ignored) {
        }
        return userList;
    }

    public void cleanUsersTable() {
        try {
            connection.createStatement().executeUpdate(String.format("TRUNCATE test.%s", userTableName));
        } catch (SQLException ignored) {
        }
    }
}

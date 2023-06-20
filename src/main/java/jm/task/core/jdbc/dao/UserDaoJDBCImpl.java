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
    private final Connection CONNECTION = Util.getConnection();

    private final String ASSERTABLE = "users";

    public UserDaoJDBCImpl() {

    }

    public void createUsersTable() {

        try (ResultSet resultSet = CONNECTION.createStatement().executeQuery(String.format("SHOW TABLES FROM test LIKE '%s'", ASSERTABLE))) {
            if (resultSet.next()) {
                System.out.println("Table exists");
            } else {
                CONNECTION.createStatement().executeUpdate(String.format("CREATE TABLE %s (id BIGINT NOT NULL AUTO_INCREMENT, name VARCHAR(255), "
                                                                        + "lastName VARCHAR(255), age TINYINT, PRIMARY KEY ( id ))", ASSERTABLE));
                System.out.println("Table create");
            }
        } catch (SQLException ignored) {
        }


    }

    public void dropUsersTable() {
        try {
            CONNECTION.createStatement().executeUpdate(String.format("DROP TABLES %s", ASSERTABLE));
        } catch (SQLException ignored) {
        }
    }

    public void saveUser(String name, String lastName, byte age) {
        try (PreparedStatement prepared = CONNECTION.prepareStatement(String.format("INSERT INTO %s (name, lastName, age) VALUES (?, ?, ?)", ASSERTABLE))) {
            prepared.setString(1, name);
            prepared.setString(2, lastName);
            prepared.setByte(3, age);
            prepared.execute();
            System.out.printf("User с именем – %s добавлен в базу данных\n", name);
        } catch (SQLException ignored) {
        }

    }

    public void removeUserById(long id) {
        try (PreparedStatement prepared = CONNECTION.prepareStatement(String.format("DELETE FROM %s WHERE id = ?", ASSERTABLE))) {
            prepared.setLong(1, id);
            prepared.executeUpdate();
            System.out.printf("User %s deleted \n", id);
        } catch (SQLException ignored) {
        }
    }

    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        try (ResultSet rs = CONNECTION.createStatement().executeQuery(String.format("SELECT * FROM %s", ASSERTABLE))) {

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
            CONNECTION.createStatement().executeUpdate(String.format("TRUNCATE test.%s", ASSERTABLE));
        } catch (SQLException ignored) {
        }
    }
}

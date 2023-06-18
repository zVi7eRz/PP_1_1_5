package jm.task.core.jdbc.dao;


import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.criteria.JpaCriteriaQuery;

import java.util.List;

public class UserDaoHibernateImpl implements UserDao {

    private final String userTableName = "users";

    public UserDaoHibernateImpl() {

    }

    @Override
    public void createUsersTable() {
        try {
            Session session = Util.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            session.createNativeQuery(String.format("CREATE TABLE %s (id BIGINT NOT NULL AUTO_INCREMENT, name VARCHAR(255), "
                            + "lastName VARCHAR(255), age TINYINT, PRIMARY KEY ( id ))", userTableName))
                    .executeUpdate();
            transaction.commit();
            session.close();
        } catch (Exception e) {
            System.out.println("Table exist");
        }
    }

    @Override
    public void dropUsersTable() {
        Session session = Util.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.createNativeQuery(String.format("DROP TABLE IF EXISTS %s", userTableName))
                .executeUpdate();
        transaction.commit();
        session.close();
    }

    @Override
    public void saveUser(String name, String lastName, byte age) {
        Session session = Util.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        User user = new User(name, lastName, age);
        session.persist(user);
        transaction.commit();
        session.close();
    }

    @Override
    public void removeUserById(long id) {
        Session session = Util.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        User user = session.get(User.class, id);
        session.remove(user);
        transaction.commit();
        session.close();
    }

    @Override
    public List<User> getAllUsers() {
        Session session = Util.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        JpaCriteriaQuery<User> cq = session.getCriteriaBuilder().createQuery(User.class);
        cq.select(cq.from(User.class));
        List<User> usersList = session.createQuery(cq).getResultList();
        transaction.commit();
        session.close();
        return usersList;
    }

    @Override
    public void cleanUsersTable() {
        Session session = Util.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        for (User user : getAllUsers()) {
            session.remove(user);
        }
        transaction.commit();
        session.close();
    }
}

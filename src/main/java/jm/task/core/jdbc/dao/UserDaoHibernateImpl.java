package jm.task.core.jdbc.dao;


import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.criteria.JpaCriteriaQuery;

import java.util.List;

public class UserDaoHibernateImpl implements UserDao {

    private final String assertable = "users";
    private Transaction transaction = null;

    public UserDaoHibernateImpl() {

    }

    @Override
    public void createUsersTable() {
        try (Session session = Util.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.createNativeQuery(String.format("CREATE TABLE %s (id BIGINT NOT NULL AUTO_INCREMENT, name VARCHAR(255), "
                            + "lastName VARCHAR(255), age TINYINT, PRIMARY KEY ( id ))", assertable))
                    .executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            System.out.println("Table exist");
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }

    @Override
    public void dropUsersTable() {
        try (Session session = Util.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.createNativeQuery(String.format("DROP TABLE IF EXISTS %s", assertable))
                    .executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            System.out.println("users not drop");
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }

    @Override
    public void saveUser(String name, String lastName, byte age) {
        try (Session session = Util.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User user = new User(name, lastName, age);
            session.persist(user);
            transaction.commit();
        } catch (Exception e) {
            System.out.printf("%s not save\n", name);
            if (transaction != null) {
                transaction.rollback();
            }
        }

    }

    @Override
    public void removeUserById(long id) {
        try (Session session = Util.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User user = session.get(User.class, id);
            session.remove(user);
            transaction.commit();
        } catch (Exception e) {
            System.out.printf("user id %s not found\n", id);
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }

    @Override
    public List<User> getAllUsers() {
        List<User> usersList;
        try (Session session = Util.getSessionFactory().openSession()) {
            JpaCriteriaQuery<User> cq = session.getCriteriaBuilder().createQuery(User.class);
            cq.select(cq.from(User.class));
            usersList = session.createQuery(cq).getResultList();
        }
        return usersList;
    }

    @Override
    public void cleanUsersTable() {
        try (Session session = Util.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            for (User user : getAllUsers()) {
                session.remove(user);
            }
            transaction.commit();
        } catch (Exception e) {
            System.out.printf("%s table not clean\n", assertable);
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }
}

package org.mangorage.mangobotcore.api.util.data;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;

public final class DatabaseHandler<ID, T extends IUniqueIdHolder<ID>> {

    // Static factory method to create a DatabaseHandler instance
    public static <ID, T extends IUniqueIdHolder<ID>> DatabaseHandler<ID, T> create(String url, String user, String pass, Class<T> entityType) {
        return new DatabaseHandler<>(url, user, pass, entityType);
    }

    private final SessionFactory sessionFactory;
    private final Class<T> entityType;

    DatabaseHandler(String url, String user, String pass, Class<T> entityType) {
        this.entityType = entityType;

        this.sessionFactory = new Configuration()
                .setProperty("hibernate.connection.url", url)
                .setProperty("hibernate.connection.username", user)
                .setProperty("hibernate.connection.password", pass)
                .setProperty("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver")
                .setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect")
                .setProperty("hibernate.hbm2ddl.auto", "update")
                .setProperty("hibernate.show_sql", "false")
                .addAnnotatedClass(entityType)
                .buildSessionFactory();
    }

    public void migrateToDatabase(List<T> fileEntities) {
        if (fileEntities.isEmpty()) return;

        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            for (T entity : fileEntities) {
                session.merge(entity);
            }

            tx.commit();
        }
    }

    public List<T> loadEntitiesFromDatabase() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from " + entityType.getName(), entityType).list();
        }
    }

    public void saveEntity(T entity) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(entity);
            tx.commit();
        }
    }

    public void removeEntity(T entity) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.createMutationQuery("delete from " + entityType.getName() + " where id = :id")
                    .setParameter("id", entity.getId())
                    .executeUpdate();
            tx.commit();
        }
    }

    public void close() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}

package main.pack.data_acces;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;


public class SessionFactoryHolder {

    private static SessionFactoryHolder instance;

    private org.hibernate.SessionFactory factory;

    private SessionFactoryHolder() {
        try {
            factory = new Configuration()
                    .addAnnotatedClass(FieldEntity.class)
                    .addAnnotatedClass(GameEntity.class)
                    .configure()
                    .buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Failed to create sessionFactory object." + ex);
            throw new ExceptionInInitializerError(ex);
        }

    }

    private static SessionFactoryHolder getInstance() {
        if (instance == null) {
            instance = new SessionFactoryHolder();
        }
        return instance;
    }

    public static SessionFactory getFactory() {
        return getInstance().factory;
    }
}
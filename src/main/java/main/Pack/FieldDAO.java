package main.Pack;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

class FieldDAO {

    private SessionFactory factory; //todo final

    FieldDAO(SessionFactory factory) {
        this.factory = factory;
    }





    void saveField(FieldEntity[] fieldEntities) {

        Transaction tx = null;
        try (Session session = factory.openSession()) {

            tx = session.beginTransaction();

            for (FieldEntity entityField : fieldEntities) {
                session.save(entityField);
                // todo sprawdzic session.saveOrUpdate();
            }
           // session.update(fieldEntities);
            tx.commit();
        } catch (HibernateException e) {
            e.printStackTrace();
            if (tx != null) tx.rollback();
        }
    }

    void updateValueFields(List<FieldEntity> fieldEntity) { //todo sprawdzic, czy V update jest potrxebny
        Transaction tx = null;
        try (Session session = factory.openSession()) {
            tx = session.beginTransaction();

            for (FieldEntity entityField : fieldEntity) {
                session.update(entityField);
                // todo sprawdzic session.saveOrUpdate();
            }
            tx.commit();
        } catch (HibernateException e) {
            e.printStackTrace();
            if (tx != null) {
                tx.rollback();
            }
        }
    }


    void updateUnFlagField(FieldEntity fieldEntity) { // todo rename updateField
        Transaction tx = null;
        try (Session session = factory.openSession()) {
            tx = session.beginTransaction(); //todo refink bo tylko 1 update jest
            session.update(fieldEntity);
            tx.commit();
        } catch (HibernateException e) {
            e.printStackTrace();
            if (tx != null) {
                tx.rollback();
            }
        }
    }


    void updateFlagField(FieldEntity fieldEntity) { // todo duplicate
        Transaction tx = null;
        try (Session session = factory.openSession()) {
            tx = session.beginTransaction();
            session.update(fieldEntity);
            tx.commit();
        } catch (HibernateException e) {
            e.printStackTrace();
            if (tx != null) {
                tx.rollback();
            }
        }
    }
}
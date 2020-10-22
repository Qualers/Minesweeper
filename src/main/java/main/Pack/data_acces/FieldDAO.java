package main.pack.data_acces;

import lombok.AllArgsConstructor;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public class FieldDAO {

    private final SessionFactory factory;

    public void saveField(List<FieldEntity> fieldEntities) {
        Transaction tx = null;
        try (Session session = factory.openSession()) {
            tx = session.beginTransaction();
            for (FieldEntity entityField : fieldEntities) {
                session.saveOrUpdate(entityField);
            }
            tx.commit();
        } catch (HibernateException e) {
            e.printStackTrace();
            if (tx != null) tx.rollback();
        }
    }

    public void saveField(FieldEntity fieldEntity) {
        this.saveField(Arrays.asList(fieldEntity));
    }
}
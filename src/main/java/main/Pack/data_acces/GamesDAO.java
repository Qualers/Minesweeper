package main.pack.data_acces;

import lombok.AllArgsConstructor;
import org.hibernate.*;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class GamesDAO {

    private final SessionFactory factory;


    public void saveGame(GameEntity game) {
        try (Session session = factory.openSession()) {
            session.save(game);
        } catch (HibernateException e) {
            e.printStackTrace();
        }
    }

    public GameEntity getGameByName(String gameName) {
        GameEntity gameEntity = null;
        try (Session session = factory.openSession()) {
            String hql = "FROM GameEntity G WHERE G.gameName = :game_name";
            Query query = session.createQuery(hql);
            query.setParameter("game_name", gameName);
            query.setMaxResults(1);
            gameEntity = (GameEntity) query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gameEntity;
    }

    public List<String> getNamesOfAllGames() {
        List<String> nameGames = new ArrayList<>();
        Transaction tx = null;
        try (Session session = factory.openSession()) {
            tx = session.beginTransaction();
            nameGames = session.createSQLQuery("SELECT gamename FROM games").list();
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (tx != null) tx.rollback();
        }
        return nameGames;
    }

    public void deleteGame(GameEntity gameEntity) {
        Transaction tx = null;
        try (Session session = factory.openSession()) {
            tx = session.beginTransaction();
            session.remove(gameEntity);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (tx != null) tx.rollback();
        }
    }

    public void updateStatusGame(GameEntity gameEntity) {
        try (Session session = factory.openSession()) {
            session.beginTransaction();
            session.update(gameEntity);
        } catch (HibernateException e) {
            e.printStackTrace();
        }
    }


    public GameEntity getEntityGameById(Integer iD) {
        GameEntity gameEntity = null;
        try (Session session = factory.openSession()) {
            session.beginTransaction();
            gameEntity = session.find(GameEntity.class, iD);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gameEntity;
    }
}
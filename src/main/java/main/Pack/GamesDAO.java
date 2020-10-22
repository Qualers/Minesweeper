package main.Pack;

import lombok.AllArgsConstructor;
import org.hibernate.*;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
class GamesDAO {

    private final SessionFactory factory;

//    GamesDAO(SessionFactory factory) {
//        this.factory = factory;
//    } // todo zamiast tego jest AllArgsConstructor (10)


    void saveGame(GameEntity game) {
        Transaction tx = null;
        try (Session session = factory.openSession()) {
            tx = session.beginTransaction();
            session.save(game);
            tx.commit();
        } catch (HibernateException e) {
            e.printStackTrace();
            if (tx != null) tx.rollback();
        }
    }

    GameEntity getGameByName(String gameName) {
        GameEntity gameEntity = null;
        Transaction tx = null;
        try (Session session = factory.openSession()) {
            tx = session.beginTransaction();

            String hql = "FROM GameEntity G WHERE G.gameName = :game_name";
            Query query = session.createQuery(hql);
            query.setParameter("game_name", gameName);
            query.setMaxResults(1);
            gameEntity = (GameEntity) query.uniqueResult();

            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (tx != null) tx.rollback();
        }
        return gameEntity;
    }

    List<String> getNamesOfAllGames() {
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

    void deleteGame(GameEntity gameEntity) {
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

    void updateStatusGame(Integer gameId, GameStatus gameStatus) {
        GameEntity gameEntity = getEntityGameById(gameId); //todo walidacja nawet jak jestem pewien po stronie serwisu jak cos pobieram
        gameEntity.setStatusGame(gameStatus); // todo element logiki biznesowej
        Transaction tx = null;
        try (Session session = factory.openSession()) {
            tx = session.beginTransaction();
            session.update(gameEntity);
            tx.commit();
        } catch (HibernateException e) {
            e.printStackTrace();
            if (tx != null) {
                tx.rollback();
            }
        }
    }

    //todo modyfikaja encji tylko w serwisie, przekazuje do DAO i tutaj tylko update itp.

    GameEntity getEntityGameById(Integer iD) {
        GameEntity gameEntity = null;
        Transaction tx = null;
        try (Session session = factory.openSession()) {
            tx = session.beginTransaction(); //todo transakcje? czu tu na pewno potrzebna (trans tylko jak modyfikuje)
            gameEntity = session.find(GameEntity.class, iD);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (tx != null) {
                tx.rollback(); // wszystko albo nic (tutaj nie ma RACZEJ sensu rollback)
            }
        }
        return gameEntity;
    }
}
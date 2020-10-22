//TODO Najtrudniej bylo po takim czasie zrozumiec dokladny sens tych "zapisek" xD
package main.Pack;

import org.hibernate.SessionFactory;

/*
 * TODO:
 *  paczki - masz wszystkie klasy w jednym worku
 *  lombok - gettery i settery nad nazwą klasy + użycie pozostałych adnotacji lomboka gdzie to ma sens
 *  dao i serwis - interfejsy
 * */
public class AppSaper {
    private SessionFactory sessionFactory = SessionFactoryHolder.getFactory();


    public static void main(String[] args) {
        AppSaper saperInstance = new AppSaper();

        try {
            saperInstance.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void start() {
        GamesDAO gamesDAO = new GamesDAO(sessionFactory);
        FieldDAO fieldDAO = new FieldDAO(sessionFactory);
        SaperService saperService = new SaperServiceImpl(gamesDAO, fieldDAO); //TODO THINK: moge w wielu miejscac
        // TODO offtopic czy dzieki kolejnym interfejsy beda mialy inne metody
        //  (z danej klasy "glownej" albo te same z innymi parametrami (np. do przeladowanych metod))
        SaperConsoleView saperConsoleView = new SaperConsoleView(saperService);
        saperConsoleView.startApp();
    }
}
// TODO po przegraniu nie zapisywac zmian na polach (tylko status gry) zeby byla mozliwosc ponownego wczytania sprzed przegranej
// TODO wątki? np. w odkrywaniu pól?
package main.pack;

import main.pack.data_acces.FieldDAO;
import main.pack.data_acces.GamesDAO;
import main.pack.data_acces.SessionFactoryHolder;
import main.pack.service.SaperService;
import main.pack.service.SaperServiceImpl;
import main.pack.view.SaperConsoleView;
import org.hibernate.SessionFactory;

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
        SaperService saperService = new SaperServiceImpl(gamesDAO, fieldDAO);
        SaperConsoleView saperConsoleView = new SaperConsoleView(saperService);
        saperConsoleView.startApp();
    }
}
package main.pack.view;


import lombok.RequiredArgsConstructor;
import main.pack.service.*;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;
import java.util.Scanner;

@RequiredArgsConstructor
public class SaperConsoleView {

    private static final String COVERED_FIELD_DISPLAY_CHARACTER = "C";
    private static final String FLAGGED_FIELD_DISPLAY_CHARACTER = "F";
    private static final String FIELD_PRINT_FORMAT = " [%s]";
    private static final String REVEALED_EMPTY_WIELD_DISPLAY_CHARACTER = " ";
    private static final Integer GAME_NAME_LENGHT = 20;
    private static final Integer Bad_Value = -5;

    private Scanner scan = new Scanner(System.in);

    private final SaperService saperService;


    public void startApp() {
        int activitySelection;
        do {
            System.out.println();
            System.out.println("Wybierz co chcesz zrobic \n1-Graj \n2-Usun wybrana gre \n3-Zakoncz program");
            activitySelection = isDigit();

        } while ((activitySelection < 1) || (activitySelection > 3));
        switch (activitySelection) {
            case 1: {
                play();
                break;
            }
            case 2: {
                displayRemoveGameMenu();
                break;
            }
            case 3: {
                endApp();
                break;
            }
        }
    }


    private void play() {
        int gameMode = gameModeSelection();
        Integer gameId = displayChooseGame(gameMode);
        startGame(gameId);
    }

    private int gameModeSelection() {
        int choice = 0;
        do {
            System.out.println("1-nowa gra \n2-wczytaj gre");
            choice = isDigit();
        } while ((choice < 1) || (choice > 2));
        return choice;
    }

    private int isDigit() {
        String check = scan.nextLine();
        if (!NumberUtils.isDigits(check)) {
            System.out.println("To nie jest cyfra, podaj wartosc jeszcze raz");
            return Bad_Value;
        }
        return Integer.parseInt(check);
    }

    private Integer displayChooseGame(int gameMode) {
        switch (gameMode) {
            case 1:
                System.out.println("Nowa gra");
                return initializationNewGame();
            case 2:
                System.out.println("Wczytaj gre");
                return chooseGameToLoad();
            default:
                throw new IllegalArgumentException("Nie ma takiej wartosci");
        }
    }


    private Integer initializationNewGame() {

        int heightBoardGame = getNumberOfRows();
        int widthBoardGame = getNumberOfColumns();
        int numberBombsOnBoard = getNumberOfBombs();
        GameDTO gameDTO = new GameDTO(heightBoardGame, widthBoardGame, numberBombsOnBoard);

        String nameGame;
        do {
            System.out.println("podaj nazwe gry, do 20 znakow");
            nameGame = scan.next();
        } while (nameGame.length() > GAME_NAME_LENGHT);
        gameDTO.setGameName(nameGame);
        return saperService.createNewGame(gameDTO);
    }

    private String viewAllGameName(String string) {
        List<String> namesAllGames = saperService.getNamesGames();
        String choosenNameGame;

        for (String game : namesAllGames) {
            System.out.println(game);
        }

        do {
            System.out.println(string);
            choosenNameGame = scan.next();
        } while (!saperService.validateGameName(namesAllGames, choosenNameGame));

        return choosenNameGame;
    }

    private Integer chooseGameToLoad() {
        boolean validateGameName = true;
        Integer gameId = 0;
        do {
            String stringToLoad = "Podaj nazwe gry do wczytania";
            String gameToLoad = viewAllGameName(stringToLoad);

            try {
                gameId = saperService.getIdGame(gameToLoad);
            } catch (RuntimeException invalidNameEx) {
                System.out.println(invalidNameEx.getMessage());
                validateGameName = false;
            }

        } while (!validateGameName);

        return gameId;
    }


    private void displayRemoveGameMenu() {
        String gameToDelete = "Podaj nazwe gry do usuniecia";

        String nameDeleteGame = viewAllGameName(gameToDelete);
        saperService.deleteTargetGame(nameDeleteGame);
        System.out.println("Usunieta");
    }

    private void endApp() {
        System.out.println("Program zakonczony");
    }

    private int getNumberOfRows() {
        int numberRows = 0;
        do {
            System.out.println("Podaj wysokosc planszy gry nie mniejsza niz 1 i nie wieksza niz 99");
            numberRows = isDigit();
        } while (numberRows < 1 || numberRows > 99);
        return numberRows;
    }

    private int getNumberOfColumns() {
        int numberColumns = 0;
        do {
            //try {
            System.out.println("Podaj szerokosc planszy gry nie mniejsza niz 1 i nie wieksza niz 99");
            numberColumns = isDigit();
//            } catch (Exception e) {
//                System.out.println("Podales bledny znak");
//                scan.nextLine();
//            }
        } while (numberColumns < 1 || numberColumns > 99);
        return numberColumns;
    }

    private int getNumberOfBombs() {
        int numberBombs = 0;
        do {
            System.out.println("Podaj ilosc bomb na planszy nie mniej niz 0 i nie wieksza niz 999");
            numberBombs = isDigit();
        } while (numberBombs < 1 || numberBombs > 9999);
        return numberBombs;
    }


    private void startGame(Integer gameId) {
        System.out.println("Witaj w grze saper, wybierz wspolrzedne na polu gry, do ktorej chcesz sie odniesc, a nastepnie odkryj badz oflaguj je.");
        GameStatus gameStatus = playGameWithId(gameId);
        displayFinalDescription(gameStatus);
    }

    private void displayFinalDescription(GameStatus gameStatus) {
        if (gameStatus == GameStatus.WIN) {
            System.out.println("GZ, ROZJEBALES SYSTEM");
        } else if (gameStatus == GameStatus.LOOSE) {
            System.out.println("KONIEC GRY, WPADLES NA BOMBE CHUJU");
        } else {
            System.out.println("Zapisales gre.");
        }
    }


    private GameStatus playGameWithId(Integer gameId) {
        GameDTO gameDTO;
        PlayerChoice playerChoice;
        do {
            gameDTO = saperService.getBoard(gameId);
            showGame(gameDTO.getBoardGameDTO());

            playerChoice = chooseMove(gameId, gameDTO);

            GameStatus gameStatus = saperService.performAction(playerChoice, gameId);
            gameDTO.setGameStatus(gameStatus);
        } while (playerChoice.getMoveChoice() != MoveChoice.END && gameDTO.getGameStatus() == GameStatus.DURING);
        saperService.setStatusGame(gameDTO.getGameStatus(), gameId);
        return gameDTO.getGameStatus();
    }

    private PlayerChoice chooseMove(Integer gameId, GameDTO gameDTO) {
        PlayerChoice playerChoice = new PlayerChoice();
        boolean isFieldReveald;

        do {
            playerChoice.setMoveChoice(chooseMove());

            if (playerChoice.getMoveChoice() == MoveChoice.END) {
                break;
            }

            playerChoosesCoordinates(gameDTO, playerChoice);
            isFieldReveald = validateMove(gameId, playerChoice);
        } while (isFieldReveald);
        return playerChoice;
    }

    private boolean validateMove(Integer gameId, PlayerChoice playerChoice) {
        boolean isFieldReveald = false;
        if ((playerChoice.getMoveChoice() == MoveChoice.FLAG) || (playerChoice.getMoveChoice() == MoveChoice.UNFLAG)) {
            if (isFieldReveald = saperService.isFieldRevealed(gameId, playerChoice)) {
                System.out.println("Nie mozesz wykonac tej operacji na odkrytym polu");
            }
        }
        return isFieldReveald;
    }

    private void playerChoosesCoordinates(GameDTO gameDTO, PlayerChoice playerChoice) {
        boolean coordinatesIsValid = true;
        do {
            try {
                System.out.println("Podaj pierwszy koordynat 'x' z zakresu:0 - " + (gameDTO.getHeight() - 1));
                playerChoice.setCoordinatesX(scan.nextInt());
                System.out.println("Podaj drugi koordynat 'y' z zakresu:0 - " + (gameDTO.getWidth() - 1));
                playerChoice.setCoordinatesY(scan.nextInt());
            } catch (Exception e) {
                System.out.println("Podales bledny znak");
                scan.nextLine();
                continue;
            }

            boolean xIsValid = playerChoice.getCoordinatesX() >= 0 && playerChoice.getCoordinatesX() <= (gameDTO.getHeight() - 1);
            boolean yIsValid = playerChoice.getCoordinatesY() >= 0 && playerChoice.getCoordinatesY() <= (gameDTO.getWidth() - 1);
            coordinatesIsValid = (xIsValid && yIsValid);

        } while (!coordinatesIsValid);
    }


    private MoveChoice chooseMove() {
        MoveChoice moveChoice;
        int choiceMove = 0;
        do {
            System.out.println("Wybierz co chcesz zrobic\n 1-Odkryj Pole         2-Oflaguj pole");
            System.out.println(" 3-OD flaguj pole      4-Zapisz gre i zakoncz");
            try {
                choiceMove = scan.nextInt();

            } catch (Exception e) {
                System.out.println("Podales bledny znak");
                scan.nextLine();
            }
        } while (choiceMove < 1 || choiceMove > 4);
        switch (choiceMove) {
            case 1:
                moveChoice = MoveChoice.REVEAL;
                System.out.println("REVEAL");
                break;

            case 2:
                moveChoice = MoveChoice.FLAG;
                System.out.println("FLAG");
                break;

            case 3:
                moveChoice = MoveChoice.UNFLAG;
                System.out.println("UNFLAG");
                break;

            case 4:
                moveChoice = MoveChoice.END;
                System.out.println("END");
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + choiceMove);
        }
        return moveChoice;
    }


    private void showGame(FieldDTO[][] field) {
        System.out.print("     ");
        printDigitRow(field[0]);
        System.out.println();
        printColumns(field);
    }

    private void printColumns(FieldDTO[][] field) {
        for (int i = 0; i < field.length; i++) {
            if (i > 9) {
                System.out.print(i + "  ");
            } else {
                System.out.print(i + "   ");
            }
            for (int j = 0; j < field[0].length; j++) {
                printField(field[i][j]);
            }
            System.out.println();
        }
    }

    private void printDigitRow(FieldDTO[] fieldDTOS) {
        for (int i = 0; i < fieldDTOS.length; i++) {
            if (i > 9) {
                System.out.print(i + "  ");
            } else {
                System.out.print(i + "   ");
            }
        }
    }

    private void printField(FieldDTO field) {
        String fieldDisplay = getFieldStatusDisplay(field);
        System.out.print(String.format(FIELD_PRINT_FORMAT, fieldDisplay));
    }

    private String getFieldStatusDisplay(FieldDTO field) {
        String fieldDisplay = "";
        switch (field.getFieldStatus()) {
            case COVERED:
                fieldDisplay = COVERED_FIELD_DISPLAY_CHARACTER;
                break;
            case FLAGGED:
                fieldDisplay = FLAGGED_FIELD_DISPLAY_CHARACTER;
                break;
            case REVEALED:
                if (field.getNumber() == 0) {
                    fieldDisplay = REVEALED_EMPTY_WIELD_DISPLAY_CHARACTER;
                } else if (field.getNumber() > 0) {
                    fieldDisplay = Integer.toString(field.getNumber());
                }
                break;
            default:
                throw new IllegalArgumentException("Field has not handled status");
        }
        return fieldDisplay;
    }
}
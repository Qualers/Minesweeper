package main.Pack;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Scanner;

@RequiredArgsConstructor
class SaperConsoleView {

    private static final String COVERED_FIELD_DISPLAY_CHARACTER = "C";
    private static final String FLAGGED_FIELD_DISPLAY_CHARACTER = "F";
    private static final String FIELD_PRINT_FORMAT = " [%s]";
    private static final String REVEALED_EMPTY_WIELD_DISPLAY_CHARACTER = " ";

    private Scanner scan = new Scanner(System.in);

    private final SaperService saperService;

//    SaperConsoleView(SaperService saperService) {
//        this.saperService = saperService;
//    } //todo zamiast tego RequiredArgsConstructor (8), tworzy konstruktor ktory zawiera( przyjmuje,ustawia) tylko pola final


    void startApp() {
        int activitySelection = 0;
        do {
            System.out.println("Wybierz co chcesz zrobic \n1-Graj \n2-Usun wybrana gre \n3-Zakoncz program");
            try {
                activitySelection = scan.nextInt();
            } catch (Exception e) {
                System.out.println("Podales bledny znak");
                scan.nextLine();
            }
        } while ((activitySelection < 1) || (activitySelection > 3));
        switch (activitySelection) {
            case 1: {
                play();
                break;
            }
            case 2: {
                removeGame();
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
        Integer gameId = startChooseGame(gameMode);
        startGame(gameId);
    }

    private int gameModeSelection() {
        int choice = 0;
        do {
            try {
                System.out.println("1-nowa gra \n2-wczytaj gre");
                choice = scan.nextInt();

            } catch (Exception e) {
                System.out.println("Podales bledny znak");
                scan.nextLine();
            }
        } while ((choice < 1) || (choice > 2));
        return choice;
    }

    private Integer startChooseGame(int gameMode) { // todo rename displayChooseGame
        switch (gameMode) {
            case 1:
                System.out.println("Nowa gra");
                return newGame();
            case 2:
                System.out.println("Wczytaj gre");
                return chooseGameToLoad();
            default:
                throw new IllegalArgumentException("bledny wartosc"); //todo yyyyyyyyyyyyy
        }
    }


    private Integer newGame() { //todo rename

        int heightBoardGame = getNumberOfRows();
        int widthBoardGame = getNumberOfColumns();
        int numberBombsOnBoard = getNumberOfBombs();
        GameDTO gameDTO = new GameDTO(heightBoardGame, widthBoardGame, numberBombsOnBoard);

        String nameGame;
        do {
            System.out.println("podaj nazwe gry, do 20 znakow"); //todo co sie stanie jak sie nie wpisze zadnego znaku
            nameGame = scan.next();
        } while (nameGame.length() > 20); // todo magic number
        gameDTO.setGameName(nameGame);
        return saperService.createNewGame(gameDTO);
    }


    private Integer chooseGameToLoad() { // todo dupliakcja kodu z usuwaniem gry (wyekstrachowac)  i np. printa czy to co sier= roznic pdac jako parametr metody
        List<String> namesAllGames;
        String nameGameToLoad = null;
        namesAllGames = saperService.getNamesGames();

        //todo nie zakladamy ze warstwa nizej cos sie dzieje (np. cos si waliduje) ale o warstwie wyzej nie mozmey zakladac (jak cos wykorzysstuje to zakladam ze to dziala) np. biblioteka czy warstwa nizej)


        for (String game : namesAllGames) {
            System.out.println(game);
        }


        do {
            System.out.println("Podaj nazwe gry do wczytania");
            nameGameToLoad = scan.next();
        } while (!saperService.validateGameName(namesAllGames, nameGameToLoad));

        Integer gameId = null;
        try {


            gameId = saperService.getIdGame(nameGameToLoad);
        } catch (Exception error) {

        }



        return gameId;
    }


    private void removeGame() { //todo displayRemoveGameMenu rename
        List<String> namesGames = saperService.getNamesGames();

        for (String o : namesGames) {
            System.out.println(o);
        }

        String nameDeleteGame;
        do {
            System.out.println("Podaj nazwe gry do usuniecia");
            nameDeleteGame = scan.next();
        } while (!saperService.validateGameName(namesGames, nameDeleteGame));
        saperService.deleteTargetGame(nameDeleteGame);
        System.out.println("Usunieta");
    }

    private void endApp() {
        System.out.println("Program zakonczony");
    }

    private int getNumberOfRows() {
        int numberRows = 0;
        do {
            try {
                System.out.println("Podaj wysokosc planszy gry nie mniejsza niz 1 i nie wieksza niz 99");
                numberRows = scan.nextInt();
            } catch (Exception e) {
                System.out.println("Podales bledny znak");
                scan.nextLine();
            }
        } while (numberRows < 1 || numberRows > 99);
        return numberRows;
    }

    private int getNumberOfColumns() {
        int numberColumns = 0;
        do {
            try {
                System.out.println("Podaj szerokosc planszy gry nie mniejsza niz 1 i nie wieksza niz 99");
                numberColumns = scan.nextInt();
            } catch (Exception e) {
                System.out.println("Podales bledny znak");
                scan.nextLine();
            }
        } while (numberColumns < 1 || numberColumns > 99);
        return numberColumns;
    }

    private int getNumberOfBombs() {
        int numberBombs = 0;
        do {
            try {
                System.out.println("Podaj ilosc bomb na planszy nie mniej niz 0 i nie wieksza niz 999");
                numberBombs = scan.nextInt();
            } catch (Exception e) {
                System.out.println("Podales bledny znak");
                scan.nextLine();
            }
        } while (numberBombs < 1 || numberBombs > 999);
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
            playerChoice = new PlayerChoice();
            gameDTO = saperService.getBoard(gameId);
            showGame(gameDTO.getBoardGameDTO());

            chooseMove(gameId, gameDTO, playerChoice); //todo return playerChoice instead of set its values
//            if (playerChoice.getMoveChoice() == MoveChoice.END) {
//                gameDTO.setGameStatus(GameStatus.SAVE);
//                break;
//            } //todo nie ptrzeba bo dodalismy warunke w while END
            gameDTO.setGameStatus(saperService.performAction(playerChoice, gameId)); //todo ekstrakcja

        } while (playerChoice.getMoveChoice() != MoveChoice.END && gameDTO.getGameStatus() == GameStatus.DURING);
        saperService.setStatusGame(gameDTO.getGameStatus(), gameId);
        return gameDTO.getGameStatus();
    }

    private void chooseMove(Integer gameId, GameDTO gameDTO, PlayerChoice playerChoice) {

        boolean isFieldReveald;
        do {
            playerChoice.setMoveChoice(chooseMove());

            if (playerChoice.getMoveChoice() == MoveChoice.END) {
                break;
            }

            playerChoosesCoordinates(gameDTO, playerChoice);
            isFieldReveald = validateMove(gameId, playerChoice);
        } while (isFieldReveald);
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
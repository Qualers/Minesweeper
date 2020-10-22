package main.Pack;

import java.util.Arrays;
import java.util.List;
import java.util.Random;


class SaperServiceImpl implements SaperService {

    private final GamesDAO gamesDAO;
    private final FieldDAO fieldDAO;

    SaperServiceImpl(GamesDAO gamesDAO, FieldDAO fieldDAO) {
        this.fieldDAO = fieldDAO;
        this.gamesDAO = gamesDAO;
    }


    @Override
    public Integer createNewGame(GameDTO gameDTO) {
        GameEntity gameEntity = newGame(gameDTO.getGameName());
        createDefaultFields(gameDTO);
        return gameEntity.getId();
    }

    private GameEntity newGame(String nameGame) {
        GameEntity gameEntity = new GameEntity(nameGame);
        gamesDAO.saveGame(gameEntity);
        return gameEntity;
    }

    private void createDefaultFields(GameDTO gameDTO) {
        int x = 0;
        GameEntity gameEntity = gamesDAO.getGameByName(gameDTO.getGameName());
        int sizeList = gameDTO.getWidth() * gameDTO.getHeight();
        FieldEntity[] fieldEntities = new FieldEntity[sizeList];
        for (int i = 0; i < gameDTO.getHeight(); i++) {
            for (int j = 0; j < gameDTO.getWidth(); j++) {
                fieldEntities[x++] = new FieldEntity(i, j, gameEntity);
            }
        }
        fieldDAO.saveField(fieldEntities);
        randomSetBombsOnBoard(gameDTO, Arrays.asList(fieldEntities));
    }


    private void randomSetBombsOnBoard(GameDTO gameDTO, List<FieldEntity> fieldEntities) {
        chooseCoordinateForBomb(gameDTO, fieldEntities);
        fieldDAO.updateValueFields(fieldEntities);
    }

    private void chooseCoordinateForBomb(GameDTO gameDTO, List<FieldEntity> fieldEntities) {

        Random coordinatesOfRandomField = new Random();
        for (int i = 0; i < gameDTO.getBombsCount(); i++) {

            do {
                int boardSize = fieldEntities.size();


                int randomFieldIndex = coordinatesOfRandomField.nextInt(boardSize);
                gameDTO.setCoordinatesBomb(randomFieldIndex);

            } while (fieldEntities.get(gameDTO.getCoordinatesBomb()).isBomb());

            fieldEntities.get(gameDTO.getCoordinatesBomb()).setValueField(-1);
            this.putDigitsAroundTheBombs(fieldEntities, gameDTO);
        }


    }

    private void putDigitsAroundTheBombs(List<FieldEntity> fieldEntityList, GameDTO gameDTO) {
        int xField = fieldEntityList.get(gameDTO.getCoordinatesBomb()).getXField(); //nie ma sensu pchac tego do DTO, lepiej zmienna od kordynatu
        int yField = fieldEntityList.get(gameDTO.getCoordinatesBomb()).getYField();
        int coordinateForIncreasingNumber;
        for (int i = -1; i < 2; i++) {
            if ((xField + i) < 0 || (xField + i) > gameDTO.getHeight() - 1) {
                continue;
            }
            for (int j = -1; j < 2; j++) {
                if ((yField + j) < 0 || (yField + j) > gameDTO.getWidth() - 1) {
                    continue;
                }
                coordinateForIncreasingNumber = getCoordinateField(gameDTO, xField + i, yField + j);

                FieldEntity fieldEntity = fieldEntityList.get(coordinateForIncreasingNumber);
                if (fieldEntity.isBomb()) {
                    continue;
                }
                fieldEntity.increaseValueField();
            }
        }
    }

    @Override
    public Integer getIdGame(String gameName) {

        try {
            List<String> namesAllGames = getNamesGames();
//todo czy na pewno dobrze
            validateGameName(namesAllGames, gameName);
        } catch (Exception invalidName) {
            throw new RuntimeException(invalidName);
        }


        GameEntity gameEntity = gamesDAO.getGameByName(gameName); //todo validate
        return gameEntity.getId(); //zrobic wyjatek i rzucic wyzej
    }

    @Override
    public boolean isFieldRevealed(int gameId, PlayerChoice playerChoice) {
        GameDTO gameDTO = getHeightAndWidthByID(gameId);
        GameEntity gameEntity = gamesDAO.getEntityGameById(gameId);
        int fieldCoordinates = getCoordinateField(gameDTO, playerChoice.getCoordinatesX(), playerChoice.getCoordinatesY());
        List<FieldEntity> fieldEntityList = gameEntity.getListField();

        boolean isFieldReveald = fieldEntityList.get(fieldCoordinates).getStatusField() == FieldStatus.REVEALED;
        return isFieldReveald;
    }


    private GameDTO getHeightAndWidthByID(int gameId) {
        //todo n+1 problem hibernate
        int height = 0;
        int width = 0;
        GameEntity gameEntity = gamesDAO.getEntityGameById(gameId);//todo na podstawie ID moge od razu pobrac cala liste encji bezposrednio z fieldDAO
        List<FieldEntity> fieldEntities = gameEntity.getListField();
        for (FieldEntity fieldEntity : fieldEntities) {
            if (fieldEntity.getXField() > height) {
                height = fieldEntity.getXField();
            }
            if (fieldEntity.getYField() > width) {
                width = fieldEntity.getYField();
            }
        }
        return new GameDTO(height + 1, width + 1);  //todo inne dto tylko z hig wid
    }


    private int getCoordinateField(GameDTO gameDTO, int x, int y) {
        return (x * gameDTO.getWidth()) + y;//todo nie wyliczam indexu tylko przeszukuje cala liste w poszukiwaniu pola z takimi X oraz Y
    }


    @Override
    public void deleteTargetGame(String nameDeleteGame) { //todo no. rzucam wyjatkiem poiom wyzej i tam przechwytuje i pokazuje komunikat
        gamesDAO.deleteGame(gamesDAO.getGameByName(nameDeleteGame)); //todo jesli na innym widoku nie bedzie walaidacji ( bo ktos ni zrobi) to dupa
        // tutaj walidacja musi byc TEZ
    }

    @Override
    public boolean validateGameName(List<String> namesGames, String nameGame) { //todo rename np. gameExists
        boolean validCompatibility = false; //todo moglbym teoretycznie wyslac dowolna liste tutaj i co?... i kupa
        //todo dlatego lepiej tutaj pobrac liste gier i sprawdzic czy podana nazwa istnieje, albo sprobowac pobrac gre o takiej nazwie

        for (String gameName : namesGames) {
            if (gameName.equals(nameGame)) {
                validCompatibility = true;
                break;
            }
        }
        return validCompatibility;
        //todo better return namesGames.contains(nameGame); i nie ma potrzeby calej tej powyzszej logiki
    }


    @Override
    public GameDTO getBoard(int gameId) {// todo
        return createBoardDTO(gameId); //todo better poporostu zwracam tablicy pol, bez gameDTO
    }

    private GameDTO createBoardDTO(int gameId) {
        GameDTO gameDTO = getHeightAndWidthByID(gameId);
        createDefaultBoard(gameDTO);
        GameEntity gameEntity = gamesDAO.getEntityGameById(gameId);
        List<FieldEntity> fieldEntityList = gameEntity.getListField();
        createFinalBoardDTO(gameDTO, fieldEntityList);
        return gameDTO;
    }

    private void createFinalBoardDTO(GameDTO gameDTO, List<FieldEntity> fieldEntityList) {

        for (int x = 0, fieldListIterator = 0; x < gameDTO.getHeight(); x++) {//todo nieczytelne
            for (int y = 0; y < gameDTO.getWidth(); y++) {
                gameDTO.getBoardGameDTO()[x][y].setFieldStatus(fieldEntityList.get(fieldListIterator).getStatusField());
                gameDTO.getBoardGameDTO()[x][y].setNumber(fieldEntityList.get(fieldListIterator).getValueField());
                fieldListIterator++;
            }
        }

    }

    private void createDefaultBoard(GameDTO gameDTO) {
        gameDTO.createBoardGameDTO(); //todo lepiej logike z tego zrbic tutaj i pozniej zrobic set na DTO

        for (int i = 0; i < gameDTO.getHeight(); i++) {
            for (int j = 0; j < gameDTO.getWidth(); j++) {
                gameDTO.getBoardGameDTO()[i][j] = new FieldDTO();
            }
        }
    }


    @Override
    public GameStatus performAction(PlayerChoice playerChoice, int gameId) {
        switch (playerChoice.getMoveChoice()) {
            case REVEAL:
                return tryRevealField(playerChoice, gameId);
            case FLAG:
                setFlagStatus(playerChoice, gameId);
                return GameStatus.DURING;
            case UNFLAG:
                setUnFlagStatus(playerChoice, gameId);
                return GameStatus.DURING;
            default:
                return GameStatus.DURING;
        }
    }

    private void setUnFlagStatus(PlayerChoice playerChoice, int gameId) {
        GameDTO gameDTO = getHeightAndWidthByID(gameId);
        GameEntity gameEntity = gamesDAO.getEntityGameById(gameId);

        int coordinateUnFlag = getCoordinateField(gameDTO, playerChoice.getCoordinatesX(), playerChoice.getCoordinatesY());
        List<FieldEntity> fieldEntityList = gameEntity.getListField();
        fieldEntityList.get(coordinateUnFlag).setStatusField(FieldStatus.COVERED);
        fieldDAO.updateUnFlagField(fieldEntityList.get(coordinateUnFlag));
    }


    private void setFlagStatus(PlayerChoice playerChoice, int gameId) {
        GameDTO gameDTO = getHeightAndWidthByID(gameId);
        GameEntity gameEntity = gamesDAO.getEntityGameById(gameId);

        int coordinateFlagField = getCoordinateField(gameDTO, playerChoice.getCoordinatesX(), playerChoice.getCoordinatesY());
        List<FieldEntity> fieldEntityList = gameEntity.getListField();
        fieldEntityList.get(coordinateFlagField).setStatusField(FieldStatus.FLAGGED);

        fieldDAO.updateFlagField(fieldEntityList.get(coordinateFlagField));
    }

    private GameStatus tryRevealField(PlayerChoice playerChoice, int gameId) {//todo metoa ma dzialac a nie probowac (bez try w nazwie)
        GameDTO gameDTO = getHeightAndWidthByID(gameId);
        GameEntity gameEntity = gamesDAO.getEntityGameById(gameId);

        int coordinateFieldToReveal = getCoordinateField(gameDTO, playerChoice.getCoordinatesX(), playerChoice.getCoordinatesY());
        List<FieldEntity> fieldEntities = gameEntity.getListField();

        if (fieldEntities.get(coordinateFieldToReveal).isBomb()) {
            gameDTO.setGameStatus(GameStatus.LOOSE);
            return gameDTO.getGameStatus();
        }

        revealField(coordinateFieldToReveal, fieldEntities, gameDTO);
        return checkWinConditions(fieldEntities);
    }

    private GameStatus checkWinConditions(List<FieldEntity> fieldEntity) {
        GameDTO gameDTO = new GameDTO();
        //todo np. increaseNumberBombs jest wykorzystywane tylko tu, wiec bez sensu trzymac to w DTO
        for (FieldEntity field : fieldEntity) {

            if (field.getStatusField() == FieldStatus.COVERED || field.getStatusField() == FieldStatus.FLAGGED) {
                gameDTO.increaseStillCoverFields();
            }
            if (field.getValueField() == -1) {
                gameDTO.increaseNumberBombs();
            }
        }
        if (gameDTO.getStillCoverFields() == gameDTO.getBombsCount()) {
            return GameStatus.WIN;
        } else {
            return GameStatus.DURING;
        }
    }


    private void revealField(int coordinateFieldToReveal, List<FieldEntity> fieldEntities, GameDTO gameDTO) {
        fieldEntities.get(coordinateFieldToReveal).setStatusField(FieldStatus.REVEALED);
        recursionRevealFields(coordinateFieldToReveal, fieldEntities, gameDTO);
    }


    private void recursionRevealFields(int cordField, List<FieldEntity> fieldEntities, GameDTO gameDTO) {
        int xField = fieldEntities.get(cordField).getXField();
        int yField = fieldEntities.get(cordField).getYField();
        for (int row = -1; row < 2; row++) {
            if ((xField + row) < 0 || (xField + row) > gameDTO.getHeight() - 1) {
                continue;
            }
            for (int column = -1; column < 2; column++) {
                if ((yField + column) < 0 || (yField + column) > gameDTO.getWidth() - 1) {
                    continue;
                }
                DataRevealField dataRevealField = new DataRevealField(gameDTO, xField, yField, row, column);
                reveal(dataRevealField, fieldEntities);
            }
        }
        fieldDAO.updateValueFields(fieldEntities);
    }

    private void reveal(DataRevealField dataRevealField, List<FieldEntity> fieldEntities) { //todo yyyyyyyyyyyy (niecyzteln)
        int coordinateRevealField = getCoordinateField(dataRevealField.getGameDTO(), dataRevealField.getXField() + dataRevealField.getRow(), dataRevealField.getYField() + dataRevealField.getColumn());
        if (fieldEntities.get(coordinateRevealField).getStatusField() == FieldStatus.COVERED && fieldEntities.get(coordinateRevealField).getValueField() > -1) {

            fieldEntities.get(coordinateRevealField).setStatusField(FieldStatus.REVEALED);
            if (fieldEntities.get(coordinateRevealField).getValueField() == 0) {

                coordinateRevealField = getCoordinateField(dataRevealField.getGameDTO(), dataRevealField
                        .getXField() + dataRevealField.getRow(), dataRevealField.getYField() + dataRevealField.getColumn());

                recursionRevealFields(coordinateRevealField, fieldEntities, dataRevealField.getGameDTO());
            }
        }
    }


    @Override
    public List<String> getNamesGames() {
        return gamesDAO.getNamesOfAllGames();
    }

    @Override
    public void setStatusGame(GameStatus gameStatus, Integer gameId) { //todo valdiate if game exists
        gamesDAO.updateStatusGame(gameId, gameStatus);
    }

}
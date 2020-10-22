package main.pack.service;


import lombok.RequiredArgsConstructor;
import main.pack.data_acces.FieldDAO;
import main.pack.data_acces.FieldEntity;
import main.pack.data_acces.GameEntity;
import main.pack.data_acces.GamesDAO;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
public class SaperServiceImpl implements SaperService {

    private static final Integer BOMB = -1;

    private final GamesDAO gamesDAO;
    private final FieldDAO fieldDAO;


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
        fieldDAO.saveField(Arrays.asList(fieldEntities));
        randomSetBombsOnBoard(gameDTO, Arrays.asList(fieldEntities));
    }


    private void randomSetBombsOnBoard(GameDTO gameDTO, List<FieldEntity> fieldEntities) {
        chooseCoordinateForBomb(gameDTO, fieldEntities);
        fieldDAO.saveField(fieldEntities);
    }

    private void chooseCoordinateForBomb(GameDTO gameDTO, List<FieldEntity> fieldEntities) {

        Random coordinatesOfRandomField = new Random();
        for (int i = 0; i < gameDTO.getBombsCount(); i++) {

            do {
                int boardSize = fieldEntities.size();


                int randomFieldIndex = coordinatesOfRandomField.nextInt(boardSize);
                gameDTO.setCoordinatesBomb(randomFieldIndex);

            } while (fieldEntities.get(gameDTO.getCoordinatesBomb()).isBomb());


            fieldEntities.get(gameDTO.getCoordinatesBomb()).setValueField(BOMB);
            this.putDigitsAroundTheBombs(fieldEntities, gameDTO);
        }


    }

    private void putDigitsAroundTheBombs(List<FieldEntity> fieldEntityList, GameDTO gameDTO) {
        int xField = fieldEntityList.get(gameDTO.getCoordinatesBomb()).getXField();
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
    public Integer getIdGame(String gameName) throws RuntimeException {

        try {
            List<String> namesAllGames = getNamesGames();
            validateGameName(namesAllGames, gameName);
        } catch (Exception invalidName) {
            throw new RuntimeException("Invalid GameName");
        }

        GameEntity gameEntity = gamesDAO.getGameByName(gameName);
        return gameEntity.getId();
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
        int height = 0;
        int width = 0;
        GameEntity gameEntity = gamesDAO.getEntityGameById(gameId);
        List<FieldEntity> fieldEntities = gameEntity.getListField();
        for (FieldEntity fieldEntity : fieldEntities) {
            if (fieldEntity.getXField() > height) {
                height = fieldEntity.getXField();
            }
            if (fieldEntity.getYField() > width) {
                width = fieldEntity.getYField();
            }
        }
        return new GameDTO(height + 1, width + 1);
    }


    private int getCoordinateField(GameDTO gameDTO, int x, int y) {
        return (x * gameDTO.getWidth()) + y;
    }


    @Override
    public void deleteTargetGame(String nameDeleteGame) {
        gamesDAO.deleteGame(gamesDAO.getGameByName(nameDeleteGame));
    }

    @Override
    public boolean validateGameName(List<String> namesGames, String nameGame) {
        boolean validCompatibility = false;
        for (String gameName : namesGames) {
            if (gameName.equals(nameGame)) {
                validCompatibility = true;
                break;
            }
        }
        return validCompatibility;
    }


    @Override
    public GameDTO getBoard(int gameId) {
        return createBoardDTO(gameId);
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

        for (int x = 0, fieldListIterator = 0; x < gameDTO.getHeight(); x++) {
            for (int y = 0; y < gameDTO.getWidth(); y++) {
                gameDTO.getBoardGameDTO()[x][y].setFieldStatus(fieldEntityList.get(fieldListIterator).getStatusField());
                gameDTO.getBoardGameDTO()[x][y].setNumber(fieldEntityList.get(fieldListIterator).getValueField());
                fieldListIterator++;
            }
        }

    }

    private void createDefaultBoard(GameDTO gameDTO) {
        gameDTO.createBoardGameDTO();

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
        fieldDAO.saveField(fieldEntityList.get(coordinateUnFlag));
    }


    private void setFlagStatus(PlayerChoice playerChoice, int gameId) {
        GameDTO gameDTO = getHeightAndWidthByID(gameId);
        GameEntity gameEntity = gamesDAO.getEntityGameById(gameId);

        int coordinateFlagField = getCoordinateField(gameDTO, playerChoice.getCoordinatesX(), playerChoice.getCoordinatesY());
        List<FieldEntity> fieldEntityList = gameEntity.getListField();
        fieldEntityList.get(coordinateFlagField).setStatusField(FieldStatus.FLAGGED);

        fieldDAO.saveField(fieldEntityList.get(coordinateFlagField));
    }

    private GameStatus tryRevealField(PlayerChoice playerChoice, int gameId) {
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
                DataRevealFieldDTO dataRevealFieldDTO = new DataRevealFieldDTO(gameDTO, xField, yField, row, column);
                reveal(dataRevealFieldDTO, fieldEntities);
            }
        }
        fieldDAO.saveField(fieldEntities);
    }

    private void reveal(DataRevealFieldDTO dataRevealFieldDTO, List<FieldEntity> fieldEntities) {
        int coordinateRevealField = getCoordinateField(dataRevealFieldDTO.getGameDTO(), dataRevealFieldDTO.getXField() + dataRevealFieldDTO.getRow(), dataRevealFieldDTO.getYField() + dataRevealFieldDTO.getColumn());
        if (fieldEntities.get(coordinateRevealField).getStatusField() == FieldStatus.COVERED && fieldEntities.get(coordinateRevealField).getValueField() > -1) {

            fieldEntities.get(coordinateRevealField).setStatusField(FieldStatus.REVEALED);
            if (fieldEntities.get(coordinateRevealField).getValueField() == 0) {

                coordinateRevealField = getCoordinateField(dataRevealFieldDTO.getGameDTO(), dataRevealFieldDTO
                        .getXField() + dataRevealFieldDTO.getRow(), dataRevealFieldDTO.getYField() + dataRevealFieldDTO.getColumn());

                recursionRevealFields(coordinateRevealField, fieldEntities, dataRevealFieldDTO.getGameDTO());
            }
        }
    }


    @Override
    public List<String> getNamesGames() {
        return gamesDAO.getNamesOfAllGames();
    }

    @Override
    public void setStatusGame(GameStatus gameStatus, Integer gameId) {

        GameEntity gameEntity = gamesDAO.getEntityGameById(gameId);
        gameEntity.setStatusGame(gameStatus);
        gamesDAO.updateStatusGame(gameEntity);
    }

}
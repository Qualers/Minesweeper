package main.pack.service;

import java.util.List;

public interface SaperService {
    Integer createNewGame(GameDTO gameDTO);

    Integer getIdGame(String gameName);

    boolean isFieldRevealed(int gameId, PlayerChoice playerChoice);

    void deleteTargetGame(String nameDeleteGame);

    boolean validateGameName(List<String> namesGames, String nameGame);

    GameDTO getBoard(int gameId);

    GameStatus performAction(PlayerChoice playerChoice, int gameId);

    List<String> getNamesGames();

    void setStatusGame(GameStatus gameStatus, Integer gameId);
}

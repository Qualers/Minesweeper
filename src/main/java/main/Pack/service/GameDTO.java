package main.pack.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class GameDTO {

    @Getter
    @Setter
    private String gameName;
    @Getter
    @Setter
    private int height = 0;
    @Getter
    @Setter
    private int width = 0;
    @Getter
    @Setter
    private int bombsCount = 0;
    @Getter
    @Setter
    private FieldDTO[][] boardGameDTO;
    @Getter
    @Setter
    private GameStatus gameStatus;
    @Getter
    @Setter
    private int stillCoverFields = 0;
    @Getter
    @Setter
    private int coordinatesBomb;


    public GameDTO(int height, int width, int bombsCount) {
        this.height = height;
        this.width = width;
        this.bombsCount = bombsCount;
        this.boardGameDTO = new FieldDTO[height][width];
        this.gameStatus = GameStatus.DURING;
    }

    GameDTO(int height, int width) {
        this.height = height;
        this.width = width;
    }

    void createBoardGameDTO() {
        this.boardGameDTO = new FieldDTO[height][width];
    }

    void increaseNumberBombs() {
        this.bombsCount++;
    }

    void increaseStillCoverFields() {
        this.stillCoverFields++;
    }
}

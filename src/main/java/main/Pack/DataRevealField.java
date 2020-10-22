package main.Pack;

import lombok.Getter;
import lombok.Setter;

class DataRevealField { //TODO DTO w nazwie
    @Getter
    @Setter
    private GameDTO gameDTO;
    @Getter
    @Setter
    private int xField;
    @Getter
    @Setter
    private int yField;
    @Getter
    @Setter
    private int row;
    @Getter
    @Setter
    private int column;

    DataRevealField(GameDTO gameDTO, int xField, int yField, int row, int column) {
        this.gameDTO = gameDTO;
        this.xField = xField;
        this.yField = yField;
        this.row = row;
        this.column = column;
    }
}

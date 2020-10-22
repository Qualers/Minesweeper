package main.Pack;

import lombok.Getter;
import lombok.Setter;

class PlayerChoice {

    @Getter @Setter
    private int coordinatesX;
    @Getter @Setter
    private int coordinatesY;
    @Getter @Setter
    private MoveChoice moveChoice;

    PlayerChoice() { // todo initialize as default field value and use @NoArgsConstructor
        this.coordinatesX = 0;
        this.coordinatesY = 0;
        this.moveChoice = null;
    }
}

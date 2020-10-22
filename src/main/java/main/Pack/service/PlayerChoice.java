package main.pack.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(force = true)
public class PlayerChoice {

    @Getter
    @Setter
    private int coordinatesX;
    @Getter
    @Setter
    private int coordinatesY;
    @Getter
    @Setter
    private MoveChoice moveChoice;
}

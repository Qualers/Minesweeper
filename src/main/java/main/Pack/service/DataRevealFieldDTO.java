package main.pack.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class DataRevealFieldDTO {
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
}

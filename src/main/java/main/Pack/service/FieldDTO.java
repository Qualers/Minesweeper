package main.pack.service;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FieldDTO {

    private FieldStatus fieldStatus = FieldStatus.COVERED;
    private int number = 0;

}
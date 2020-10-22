package main.Pack;

import lombok.Getter;
import lombok.Setter;

@Getter
// @Setter(AccessLevel.PACKAGE)
@Setter
class FieldDTO {

    private FieldStatus fieldStatus = FieldStatus.COVERED;
    private int number = 0;

}
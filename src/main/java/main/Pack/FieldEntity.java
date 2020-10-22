package main.Pack;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "field")
public class FieldEntity implements Serializable {
    @Column(name = "id", unique = true, nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gameid")
    @Getter @Setter
    private GameEntity gameid;

    @Column(name = "xField")
    @Getter @Setter
    private Integer xField;

    @Column(name = "yField")
    @Getter @Setter
    private Integer yField;

    @Column(name = "statusField")
    @Enumerated(EnumType.STRING)
    @Getter @Setter
    private FieldStatus statusField;

    @Column(name = "valueField")
    @Getter @Setter
    private Integer valueField;

    FieldEntity(int xField, int yField, GameEntity gameid) {
        this.xField = xField;
        this.yField = yField;
        this.gameid = gameid;
        this.statusField = FieldStatus.COVERED;
        this.valueField = 0;
    }

    public FieldEntity() {
    }

    void increaseValueField() {
        this.valueField++;
    }

    boolean isBomb() {
        return valueField == -1;
    }
}
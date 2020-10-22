package main.Pack;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "games")
@NoArgsConstructor
class GameEntity implements Serializable {

    @Column(name = "id", unique = true, nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Integer id;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "gameid", cascade = CascadeType.REMOVE)
    @Getter
    @Setter
    private List<FieldEntity> listField;

    @Column(name = "gamename", unique = true)
    @Getter
    @Setter
    private String gameName;

    @Column(name = "statusgame")
    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    private GameStatus statusGame;

    GameEntity(String gameName) {
        this.gameName = gameName;
        setStatusGame(GameStatus.DURING);
    }
}

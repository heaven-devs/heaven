package ga.heaven.model;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Info { // Таблица: Информация (Info) - документы, правила
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String area; // область правил (key): транспортировка, обустройства дома детеныша, обустройства дома для взрослой особи, советы кинолога, проверенные кинологи, причина отказа

    private String instructions; // правила (инструкции)

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Info info = (Info) o;
        return Objects.equals(area, info.area) && Objects.equals(instructions, info.instructions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(area, instructions);
    }
}

package ga.heaven.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "shelter")
public class Shelter { // Таблица: Приют
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id; // уникальный id
    private String name; // название приюта питомцев
    private String address; // адрес
    private String locationMap; // ссылка на схему проезда
    //@JsonIgnore
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "volunteer_shelter",
            joinColumns = @JoinColumn(name = "shelter_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "volunteer_id", referencedColumnName = "id"))
    private Set<Volunteer> volunteers = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Shelter shelter = (Shelter) o;

        if (id != shelter.id) return false;
        if (name != null ? !name.equals(shelter.name) : shelter.name != null) return false;
        if (address != null ? !address.equals(shelter.address) : shelter.address != null) return false;
        return locationMap != null ? locationMap.equals(shelter.locationMap) : shelter.locationMap == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (locationMap != null ? locationMap.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Shelter{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}

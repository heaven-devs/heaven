package ga.heaven.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

// Таблица: Клиент (Customer) в БД
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long chatId;
    private String surname;
    private String name;
    private String secondName;
    private String phone;
    private String address;

    @OneToOne(cascade = CascadeType.ALL)
    private CustomerContext customerContext;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Customer customer = (Customer) o;
        return id != null && Objects.equals(id, customer.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

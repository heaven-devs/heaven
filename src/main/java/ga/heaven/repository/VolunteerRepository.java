package ga.heaven.repository;

import ga.heaven.model.Shelter;
import ga.heaven.model.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {
    
   // List<Volunteer> findVolunteersBySheltersContainsIgnoreCase(Set<Shelter> shelters);


}

package ga.heaven.service;

import ga.heaven.model.Shelter;
import ga.heaven.repository.ShelterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShelterServiceTest {

    @InjectMocks
    private ShelterService shelterService;

    @Mock
    private ShelterRepository shelterRepository;

    private List<Shelter> expectedShelterList;
    private Shelter expectedShelter;

    @BeforeEach
    private void getInitialTestShelters() {
        expectedShelterList = List.of(
                new Shelter(1, "Shelter 1", "address", "location",null),
                new Shelter(2, "Shelter 2", "address", "location", null)
        );
        expectedShelter = expectedShelterList.get(0);
    }

    @Test
    void findAllShelters() {
        when(shelterRepository.findAll()).thenReturn(expectedShelterList);
        List<Shelter> actual = shelterService.findAll();
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expectedShelterList);
    }

    @Test
    void findShelterById() {
        Long testId = expectedShelter.getId();
        when(shelterRepository.findById(testId)).thenReturn(Optional.of(expectedShelter));
        Shelter actual = shelterService.findById(testId);
        assertThat(actual).isEqualTo(expectedShelter);
    }

    @Test
    void createShelter() {
        when(shelterRepository.save(expectedShelter)).thenReturn(expectedShelter);
        Shelter actual = shelterService.create(expectedShelter);
        assertThat(actual).isEqualTo(expectedShelter);
    }

    @Test
    void updateShelter() {
        Long testId = expectedShelter.getId();
        when(shelterRepository.findById(testId)).thenReturn(Optional.ofNullable(expectedShelter));
        when(shelterRepository.save(expectedShelter)).thenReturn(expectedShelter);
        Shelter actual = shelterService.update(expectedShelter);
        assertThat(actual).isEqualTo(expectedShelter);
    }

    @Test
    void deleteShelter() {
        Long testId = expectedShelter.getId();
        when(shelterRepository.findById(testId)).thenReturn(Optional.ofNullable(expectedShelter));
        Shelter actual = shelterService.delete(testId);
        assertThat(actual).isEqualTo(expectedShelter);
    }


}

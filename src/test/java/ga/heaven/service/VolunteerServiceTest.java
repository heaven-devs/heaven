package ga.heaven.service;

import com.pengrad.telegrambot.TelegramBot;
import ga.heaven.model.Volunteer;
import ga.heaven.repository.VolunteerRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VolunteerServiceTest {

    @Mock
    private VolunteerRepository volunteerRepository;

    @MockBean
    private TelegramBot bot;

    @InjectMocks
    private VolunteerService volunteerService;

    private List<Volunteer> volunteersForTest() {

        Volunteer volunteer1 = createTestVolunteer(1L, 123L, "Blink", "Amanda", "-", "12345", "123 Second Creek Rd, #1");
        Volunteer volunteer2 = createTestVolunteer(2L, 124L, "Brouni", "Sandra", "-", "67890", "145 Avery Ranch Rd, #2");

        List<Volunteer> volunteers = new ArrayList<>();
        volunteers.add(volunteer1);
        volunteers.add(volunteer2);

        return volunteers;
    }

    private Volunteer createTestVolunteer(Long id, Long chatId, String surname, String name, String secondName, String phone, String address) {
        Volunteer volunteer = new Volunteer();
        volunteer.setId(id);
        volunteer.setChatId(chatId);
        volunteer.setSurname(surname);
        volunteer.setName(name);
        volunteer.setSecondName(secondName);
        volunteer.setPhone(phone);
        volunteer.setAddress(address);
        return (volunteer);
    }

    private Volunteer testVolunteer() {
        Volunteer v1 = new Volunteer();
        v1.setId(1L);
        v1.setChatId(123L);
        v1.setSurname("Blink");
        v1.setName("Amanda");
        v1.setSecondName("-");
        v1.setPhone("12345");
        v1.setAddress("123 Second Creek Rd, #1");
        return v1;

    }

    private Volunteer testVolunteerWrong() {
        Volunteer v2 = new Volunteer();
        v2.setId(3L);
        v2.setChatId(789L);
        v2.setSurname("Faber");
        v2.setName("Susan");
        v2.setSecondName("-");
        v2.setPhone("09876");
        v2.setAddress("453 Parmer Ln, #3");
        return v2;
    }

    private Volunteer testVolunteerUpdate() {
        Volunteer v3 = new Volunteer();
        v3.setId(3L);
        v3.setChatId(564L);
        v3.setSurname("Smith");
        v3.setName("Susan");
        v3.setSecondName("-");
        v3.setPhone("67890");
        v3.setAddress("453 Parmer Ln, #3");
        return v3;

    }

    @Test
    void testFindAllVolunteers(){
        List<Volunteer> volunteers = volunteersForTest();
        when(volunteerRepository.findAll()).thenReturn(volunteers);
        List<Volunteer> actual = volunteerService.findAllVolunteers();
        Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(volunteers);
    }

    @Test
    void  testFindVolunteerById(){
        when(volunteerRepository.findById(1L)).thenReturn(Optional.of(testVolunteer()));
        when(volunteerRepository.findById(3L)).thenReturn(Optional.of(testVolunteerWrong()));
        Assertions.assertThat(volunteerService.findVolunteerById(1L)).isEqualTo(testVolunteer());
        Assertions.assertThat(volunteerService.findVolunteerById(3L)).isNotEqualTo(testVolunteer());
    }

    @Test
    void  testCreateVolunteer(){
        when(volunteerRepository.
                save(testVolunteer())).
                thenReturn(testVolunteer());
        assertThat(volunteerService.
                createVolunteer(testVolunteer())).
                isEqualTo(testVolunteer());
    }

    @Test
    public void testUpdateVolunteer() {
        Volunteer expected = createTestVolunteer(1L, 123L, "Blink", "Amanda", "-", "12345", "123 Second Creek Rd, #1");
        when(volunteerRepository.findById(expected.getId())).thenReturn(Optional.ofNullable(expected));
        when(volunteerRepository.save(expected)).thenReturn(expected);
        Volunteer actual = volunteerService.updateVolunteer(expected);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void deleteVolunteer() {
    when(volunteerRepository.findById(2L)).thenReturn(Optional.empty());
    Volunteer expected = null;
    Volunteer actual = volunteerService.deleteVolunteer(2L);
    Assertions.assertThat(actual).isEqualTo(expected);
    Volunteer volunteer = createTestVolunteer(3L, 123L, "Blink", "Amanda", "-", "12345", "123 Second Creek Rd, #1");
    when(volunteerRepository.findById(3L)).thenReturn(Optional.of(volunteer));

    expected = volunteer;
    actual = volunteerService.deleteVolunteer(3L);
    Assertions.assertThat(actual).isEqualTo(expected);
    }

}

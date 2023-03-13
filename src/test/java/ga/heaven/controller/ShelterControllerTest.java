package ga.heaven.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ga.heaven.model.Shelter;
import ga.heaven.repository.ShelterRepository;
import ga.heaven.service.*;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
public class ShelterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private ShelterController shelterController;

    @SpyBean
    private ShelterService shelterService;

    @MockBean
    private ShelterRepository shelterRepository;

    @MockBean
    private CustomerService customerService;
    @MockBean
    private InfoService infoService;
    @MockBean
    private PetService petService;
    @MockBean
    private ReportService reportService;
    @MockBean
    private VolunteerService volunteerService;
    @MockBean
    private PeripheralService peripheralService;
    @MockBean
    private BreedService breedService;

    private List<Shelter> expectedShelterList;
    private Shelter expectedShelter;
    private String urlPath = "/shelter";
    private Gson gson = new Gson();

    @BeforeEach
    private void getInitialTestShelters() {
        expectedShelterList = List.of(
                new Shelter(1, "Shelter 1", "address", "location",null),
                new Shelter(2, "Shelter 2", "address", "location",null)
        );
        expectedShelter = expectedShelterList.get(0);
    }

    @Test
    void findAllShelters() throws Exception {
        when(shelterRepository.findAll()).thenReturn(expectedShelterList);

        MockHttpServletResponse response = mockMvc.perform(get(urlPath))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        Type listOfMyClassObjects = new TypeToken<List<Shelter>>() {
        }.getType();

        List<Shelter> actual = gson.fromJson(response.getContentAsString(), listOfMyClassObjects);
        assertThat(actual)
                .containsExactlyInAnyOrderElementsOf(expectedShelterList)
                .isEqualTo(expectedShelterList);
    }

    @Test
    void findShelterByIdPositive() throws Exception {
        Long testId = expectedShelter.getId();
        when(shelterRepository.findById(testId)).thenReturn(Optional.ofNullable(expectedShelter));

        MockHttpServletResponse response = mockMvc.perform(get(urlPath + "/" + testId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Shelter actual = gson.fromJson(response.getContentAsString(), Shelter.class);
        assertThat(actual).isEqualTo(expectedShelter);
    }

    @Test
    void findShelterByIdNegative() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get(urlPath + "/" + expectedShelter.getId()))
                .andExpect(status().isNotFound())
                .andReturn().getResponse();

        Shelter actual = gson.fromJson(response.getContentAsString(), Shelter.class);
        assertThat(actual).isNull();
    }

    @Test
    void createShelter() throws Exception {
        when(shelterRepository.save(expectedShelter)).thenReturn(expectedShelter);

        JSONObject jo = new JSONObject();
        jo.put("id", expectedShelter.getId());
        jo.put("name", expectedShelter.getName());
        jo.put("address", expectedShelter.getAddress());
        jo.put("locationMap", expectedShelter.getLocationMap());

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders
                        .post(urlPath)
                        .content(jo.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedShelter.getId()))
                .andReturn().getResponse();

        Shelter actual = gson.fromJson(response.getContentAsString(), Shelter.class);
        assertThat(actual).isEqualTo(expectedShelter);
    }

    @Test
    void updateShelterPositive() throws Exception {
        when(shelterRepository.findById(expectedShelter.getId())).thenReturn(Optional.ofNullable(expectedShelter));
        when(shelterRepository.save(expectedShelter)).thenReturn(expectedShelter);

        JSONObject jo = new JSONObject();
        jo.put("id", expectedShelter.getId());
        jo.put("name", expectedShelter.getName());
        jo.put("address", expectedShelter.getAddress());
        jo.put("locationMap", expectedShelter.getLocationMap());

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders
                        .put(urlPath)
                        .content(jo.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedShelter.getId()))
                .andReturn().getResponse();

        Shelter actual = gson.fromJson(response.getContentAsString(), Shelter.class);
        assertThat(actual).isEqualTo(expectedShelter);
    }

    @Test
    void updateShelterNegative() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("id", expectedShelter.getId());
        jo.put("name", expectedShelter.getName());
        jo.put("address", expectedShelter.getAddress());
        jo.put("locationMap", expectedShelter.getLocationMap());

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders
                        .put(urlPath)
                        .content(jo.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn().getResponse();

        Shelter actual = gson.fromJson(response.getContentAsString(), Shelter.class);
        assertThat(actual).isNull();
    }

    @Test
    void deleteShelterPositive() throws Exception {
        when(shelterRepository.findById(expectedShelter.getId())).thenReturn(Optional.ofNullable(expectedShelter));

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders
                        .delete(urlPath + "/" + expectedShelter.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        Shelter actual = gson.fromJson(response.getContentAsString(), Shelter.class);
        assertThat(actual).isEqualTo(expectedShelter);
    }

    @Test
    void deleteShelterNegative() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders
                        .delete(urlPath + "/" + expectedShelter.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn().getResponse();

        Shelter actual = gson.fromJson(response.getContentAsString(), Shelter.class);
        assertThat(actual).isNull();
    }

}


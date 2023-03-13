package ga.heaven.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ga.heaven.model.Breed;
import ga.heaven.repository.BreedRepository;
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
public class BreedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private BreedController breedController;

    @SpyBean
    private BreedService breedService;

    @MockBean
    private BreedRepository breedRepository;

    @MockBean
    private CustomerService customerService;
    @MockBean
    private InfoService infoService;
    @MockBean
    private PetService petService;
    @MockBean
    private ReportService reportService;
    @MockBean
    private ShelterService shelterService;
    @MockBean
    private VolunteerService volunteerService;
    @MockBean
    private PeripheralService peripheralService;

    private List<Breed> expectedBreedList;
    private Breed expectedBreed;
    private String urlPath = "/breed";
    private Gson gson = new Gson();

    @BeforeEach
    private void getInitialTestBreeds() {
        expectedBreedList = List.of(
                new Breed(1, "Sheepdog", "Puppy recommendation", "Recommendation for an adult dog", 10),
                new Breed(2, "Poodle", "Puppy recommendation", "Recommendation for an adult dog", 12),
                new Breed(3, "Collie", "Puppy recommendation", "Recommendation for an adult dog", 20)
        );
        expectedBreed = expectedBreedList.get(0);
    }

    @Test
    void findAllBreeds() throws Exception {
        when(breedRepository.findAll()).thenReturn(expectedBreedList);

        MockHttpServletResponse response = mockMvc.perform(get(urlPath))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        Type listOfMyClassObjects = new TypeToken<List<Breed>>() {
        }.getType();

        List<Breed> actual = gson.fromJson(response.getContentAsString(), listOfMyClassObjects);

        assertThat(actual)
                .containsExactlyInAnyOrderElementsOf(expectedBreedList)
                .isEqualTo(expectedBreedList);
    }

    @Test
    void findBreedByIdPositive() throws Exception {
        Long testId = expectedBreed.getId();
        when(breedRepository.findById(testId)).thenReturn(Optional.ofNullable(expectedBreed));

        MockHttpServletResponse response = mockMvc.perform(get(urlPath + "/" + testId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Breed actual = gson.fromJson(response.getContentAsString(), Breed.class);
        assertThat(actual).isEqualTo(expectedBreed);
    }

    @Test
    void findBreedByIdNegative() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get(urlPath + "/" + expectedBreed.getId()))
                .andExpect(status().isNotFound())
                .andReturn().getResponse();

        Breed actual = gson.fromJson(response.getContentAsString(), Breed.class);
        assertThat(actual).isNull();
    }

    @Test
    void createBreed() throws Exception {
        when(breedRepository.save(expectedBreed)).thenReturn(expectedBreed);

        JSONObject jo = new JSONObject();
        jo.put("id", expectedBreed.getId());
        jo.put("breed", expectedBreed.getBreed());
        jo.put("recommendationsChild", expectedBreed.getRecommendationsChild());
        jo.put("recommendationsAdult", expectedBreed.getRecommendationsAdult());
        jo.put("adultPetFromAge", expectedBreed.getAdultPetFromAge());

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders
                        .post(urlPath)
                        .content(jo.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedBreed.getId()))
                .andReturn().getResponse();

        Breed actual = gson.fromJson(response.getContentAsString(), Breed.class);
        assertThat(actual).isEqualTo(expectedBreed);
    }

    @Test
    void updateBreedPositive() throws Exception {
        when(breedRepository.findById(expectedBreed.getId())).thenReturn(Optional.ofNullable(expectedBreed));
        when(breedRepository.save(expectedBreed)).thenReturn(expectedBreed);

        JSONObject jo = new JSONObject();
        jo.put("id", expectedBreed.getId());
        jo.put("breed", expectedBreed.getBreed());
        jo.put("recommendationsChild", expectedBreed.getRecommendationsChild());
        jo.put("recommendationsAdult", expectedBreed.getRecommendationsAdult());
        jo.put("adultPetFromAge", expectedBreed.getAdultPetFromAge());

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders
                        .put(urlPath)
                        .content(jo.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedBreed.getId()))
                .andReturn().getResponse();

        Breed actual = gson.fromJson(response.getContentAsString(), Breed.class);
        assertThat(actual).isEqualTo(expectedBreed);
    }

    @Test
    void updateBreedNegative() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("id", expectedBreed.getId());
        jo.put("breed", expectedBreed.getBreed());
        jo.put("recommendationsChild", expectedBreed.getRecommendationsChild());
        jo.put("recommendationsAdult", expectedBreed.getRecommendationsAdult());
        jo.put("adultPetFromAge", expectedBreed.getAdultPetFromAge());

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders
                        .put(urlPath)
                        .content(jo.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn().getResponse();

        Breed actual = gson.fromJson(response.getContentAsString(), Breed.class);
        assertThat(actual).isNull();
    }

    @Test
    void deleteBreedPositive() throws Exception {
        when(breedRepository.findById(expectedBreed.getId())).thenReturn(Optional.ofNullable(expectedBreed));

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders
                        .delete(urlPath + "/" + expectedBreed.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        Breed actual = gson.fromJson(response.getContentAsString(), Breed.class);
        assertThat(actual).isEqualTo(expectedBreed);
    }

    @Test
    void deleteBreedNegative() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders
                        .delete(urlPath + "/" + expectedBreed.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn().getResponse();

        Breed actual = gson.fromJson(response.getContentAsString(), Breed.class);
        assertThat(actual).isNull();
    }
}

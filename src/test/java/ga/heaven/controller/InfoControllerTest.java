package ga.heaven.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ga.heaven.model.Customer;
import ga.heaven.model.Info;
import ga.heaven.repository.InfoRepository;
import ga.heaven.service.*;
import org.json.JSONObject;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest
class InfoControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @InjectMocks
    private InfoController infoController;

    @SpyBean
    private InfoService infoService;

    @MockBean
    private InfoRepository infoRepositoryMock;


    @MockBean
    private BreedService breedService;
    @MockBean
    private PeripheralService peripheralService;

    @MockBean
    private PetService petService;
    @MockBean
    private ReportService reportService;
    @MockBean
    private ShelterService shelterService;
    @MockBean
    private VolunteerService volunteerService;
    @MockBean
    private CustomerController customerController;
    @MockBean
    private CustomerService customerService;

    private Gson gson = new Gson();

    private List<Info> getInitialTestInfoList() {
        Info info1 = getTestInfo(1L, "area1", "+instructions1");
        Info info2 = getTestInfo(2L, "area2", "+instructions2");

        List<Info> infoList = new ArrayList<>();
        infoList.add(info1);
        infoList.add(info2);

        return infoList;
    }

    private Info getTestInfo(long id, String area, String instructions) {
        Info info = new Info();
        info.setId(id);
        info.setArea(area);
        info.setInstructions(instructions);
        return info;
    }

    @Test
    void getAllInfoRecords() throws Exception{
        List<Info> infoList = getInitialTestInfoList();
        when(infoRepositoryMock.findAll()).thenReturn(infoList);

        MockHttpServletResponse response = mockMvc.perform(get("/info"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Type listOfMyClassObject = new TypeToken<ArrayList<Info>>() {}.getType();
        List<Info> actual = gson.fromJson(response.getContentAsString(), listOfMyClassObject);
        assertThat(actual).containsExactlyInAnyOrderElementsOf(infoList);
    }

    @Test
    void getInfoById() throws Exception{
        Info info = getTestInfo(1L, "area1", "+instructions1");


        when(infoRepositoryMock.findById(1L)).thenReturn(Optional.of(info));
        when(infoRepositoryMock.findById(35L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/info/35"))
                .andExpect(status().isNotFound());

        MockHttpServletResponse response = mockMvc.perform((get("/info/1")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Info actual = gson.fromJson(response.getContentAsString(), Info.class);
        assertThat(actual).isEqualTo(info);
    }

    @Test
    void createInfo() throws Exception{
        Info info = getTestInfo(1L, "area1", "instructions1");
        when(infoRepositoryMock.save(info)).thenReturn(info);

        JSONObject jo = new JSONObject();
        jo.put("id", 1L);
        jo.put("area", "area1");
        jo.put("instructions", "instructions1");

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/info")
                        .content(jo.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.area").value("area1"));
    }

    @Test
    void updateInfo() throws Exception{
        when(infoRepositoryMock.findInfoById(35L)).thenReturn(Optional.empty());

        JSONObject newInfoJson = new JSONObject();
        newInfoJson.put("id", 35L);
        newInfoJson.put("area", "area35");
        newInfoJson.put("instructions", "instructions35");

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/info")
                        .content(newInfoJson.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        Info info = getTestInfo(1L, "area1", "instructions1");
        //when(infoRepositoryMock.findInfoById(1L)).thenReturn(Optional.of(info));
        when(infoRepositoryMock.findById(1L)).thenReturn(Optional.of(info));
        when(infoRepositoryMock.save(info)).thenReturn(info);

        JSONObject newInfoJson2 = new JSONObject();
        newInfoJson2.put("id", 1L);
        newInfoJson2.put("area", "area1");
        newInfoJson2.put("instructions", "instructions1");

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/info")
                        .content(newInfoJson2.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    void removeInfo() throws Exception{
        when(infoRepositoryMock.findInfoById(35L)).thenReturn(Optional.empty()); // нет такого
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/info/35")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Info info = getTestInfo(1L, "area1", "instructions1");
        //when(infoRepositoryMock.findInfoById(1L)).thenReturn(Optional.of(info));
        when(infoRepositoryMock.findById(1L)).thenReturn(Optional.of(info));

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders
                        .delete("/info/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        Info actual = gson.fromJson(response.getContentAsString(), Info.class);
        assertThat(actual).isEqualTo(info);
    }
}
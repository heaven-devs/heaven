package ga.heaven.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import ga.heaven.model.Pet;
import ga.heaven.model.Report;
import ga.heaven.model.Volunteer;
import ga.heaven.repository.ReportRepository;
import ga.heaven.service.ReportService;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.AssertionsForClassTypes;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ReportController.class)
public class ReportControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ReportRepository reportRepository;

    @SpyBean
    private ReportService reportService;

    @InjectMocks
    private ReportController reportController;

    private Gson gson = new Gson();

    @Test
    public void testFindAllReportFindAllReport() throws Exception{
        List <Report> reports = reportsForTest();
        when(reportRepository.findAll()).thenReturn(reports);
        mockMvc.perform(get("/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse();
    }

    @Test
    public void testFindReportById() throws Exception {
        Report report1 = createTestReport(1L, "test1", null, "12.jpeg", 1000L, null, null, null);

        when(reportRepository.findById(report1.getId())).thenReturn(Optional.of(report1));
        MockHttpServletResponse response = mockMvc.perform(get("/report/" + report1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        Report actual = new ObjectMapper().readValue(response.getContentAsString(), report1.getClass());
        assertThat(actual.getId()).isEqualTo(report1.getId());
        assertThat(actual.getPetReport()).isEqualTo(report1.getPetReport());
        assertThat(actual.getPhoto()).isEqualTo(report1.getPhoto());
        assertThat(actual.getDate()).isEqualTo(report1.getDate());

        when(reportRepository.findById(report1.getId())).thenReturn(Optional.empty());
        response = mockMvc.perform(get("/report/" + report1.getId()))
                .andExpect(status().isNotFound())
                .andReturn().getResponse();
        actual = gson.fromJson(response.getContentAsString(), report1.getClass());
        assertThat(actual).isNull();
    }

    @Test
    public void testDeleteReport() throws Exception {
        Report report1 = createTestReport(1L, "test1", null, "12.jpeg", 1000L, null, null, null);

        when(reportRepository.findById(report1.getId())).thenReturn(Optional.of(report1));
        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders
                        .delete("/report/" + report1.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse();
        Report actual = new ObjectMapper().readValue(response.getContentAsString(), report1.getClass());
        assertThat(actual.getId()).isEqualTo(report1.getId());
        assertThat(actual.getPetReport()).isEqualTo(report1.getPetReport());
        assertThat(actual.getPhoto()).isEqualTo(report1.getPhoto());
        assertThat(actual.getDate()).isEqualTo(report1.getDate());

        when(reportRepository.findById(report1.getId())).thenReturn(Optional.empty());
        response = mockMvc.perform(MockMvcRequestBuilders
                        .delete("/report/" + report1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn().getResponse();
        actual = gson.fromJson(response.getContentAsString(), report1.getClass());
        assertThat(actual).isNull();
    }

    @Test
    public void updateReport () throws Exception{
        Long id = 1L;
        String oldPetReport = "test1";
        LocalDateTime oldDate = LocalDate.of(2023, 3, 5).atStartOfDay();
        String oldFilePath = "test1";
        Long oldFileSize = 1000L;
        String oldMediaType = null;
        byte[] oldPhoto = null;
        Pet petId = null;

        String newPetReport = "test2";
        LocalDateTime newDate = LocalDate.of(2023, 4, 5).atStartOfDay();
        String newFilePath = "test2";
        Long newFileSize = 950L;
        String newMediaType = null;
        byte[] newPhoto = null;

        JSONObject reportObj = new JSONObject();
        reportObj.put("id", id);
        reportObj.put("petReport", newPetReport);
        reportObj.put("data", newDate);
        reportObj.put("filePath", newFilePath);
        reportObj.put("fileSize", newFileSize);
        reportObj.put("mediaType", newMediaType);
        reportObj.put("photo", newPhoto);
        reportObj.put("petId", petId);

        Report report = new Report();
        report.setId(id);
        report.setPetReport(oldPetReport);
        report.setDate(oldDate);
        report.setFilePath(oldFilePath);
        report.setFileSize(oldFileSize);
        report.setMediaType(oldMediaType);
        report.setPhoto(oldPhoto);
        report.setPet(petId);

        Report updateReport = new Report();
        report.setId(id);
        report.setPetReport(newPetReport);
        report.setDate(newDate);
        report.setFilePath(newFilePath);
        report.setFileSize(newFileSize);
        report.setMediaType(newMediaType);
        report.setPhoto(newPhoto);
        report.setPet(petId);

        when(reportRepository.findById(id)).thenReturn(Optional.of(report));
        when(reportRepository.save(any(Report.class))).thenReturn(updateReport);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders
                        .put("/report")
                        .content(reportObj.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.petReport").value(newPetReport))
                .andExpect(jsonPath("$.data").value(newDate))
                .andExpect(jsonPath("$.filePath").value(newFilePath))
                .andExpect(jsonPath("$.fileSize").value(newFileSize))
                .andExpect(jsonPath("$.mediaType").value(newMediaType))
                .andExpect(jsonPath("$.photo").value(newPhoto))
                .andExpect(jsonPath("$.petId").value(petId))
                .andReturn().getResponse();
        Gson gson = new Gson();
        Report actual = gson.fromJson(response.getContentAsString(), report.getClass());
        Assertions.assertThat(actual).isEqualTo(report);

        when(reportRepository.findById(id)).thenReturn(Optional.empty());
        response = mockMvc.perform(MockMvcRequestBuilders
                        .put("/report")
                        .content(reportObj.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn().getResponse();
        gson = new Gson();
        actual = gson.fromJson(response.getContentAsString(), report.getClass());
        AssertionsForClassTypes.assertThat(actual).isNull();
    }

    @Test
    public void createReport ()throws Exception{
        Long id = 1L;
        String petReport = "test1";
        LocalDateTime date = LocalDate.of(2023, 3, 5).atStartOfDay();
        String filePath = "test1";
        Long fileSize = 1000L;
        String mediaType = null;
        byte[] photo = null;
        Pet petId = null;

        JSONObject reportObj = new JSONObject();
        reportObj.put("id", id);
        reportObj.put("petReport", petReport);
        reportObj.put("data", date);
        reportObj.put("filePath", filePath);
        reportObj.put("fileSize", fileSize);
        reportObj.put("mediaType", mediaType);
        reportObj.put("photo", photo);
        reportObj.put("petId", petId);

        Report report = new Report();
        report.setId(id);
        report.setPetReport(petReport);
        report.setDate(date);
        report.setFilePath(filePath);
        report.setFileSize(fileSize);
        report.setMediaType(mediaType);
        report.setPhoto(photo);
        report.setPet(petId);


//        Volunteer volunteer = new Volunteer();
//        volunteer.setId(id);
//        volunteer.setChatId(chatId);
//        volunteer.setSurname(surname);
//        volunteer.setName(name);
//        volunteer.setSecondName(secondName);
//        volunteer.setPhone(phone);
//        volunteer.setAddress(address);
        when(reportRepository.save(any(Report.class))).thenReturn(report);
        when(reportRepository.findById(id)).thenReturn(Optional.of(report));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/report")
                        .content(reportObj.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.petReport").value(petReport))
                .andExpect(jsonPath("$.data").value(date))
                .andExpect(jsonPath("$.filePath").value(filePath))
                .andExpect(jsonPath("$.fileSize").value(fileSize))
                .andExpect(jsonPath("$.mediaType").value(mediaType))
                .andExpect(jsonPath("$.photo").value(photo))
                .andExpect(jsonPath("$.petId").value(petId));

    }


    private List<Report> reportsForTest() {

        LocalDateTime d = LocalDate.of(2023, 3, 5).atStartOfDay();
        Report report1 = createTestReport(1L, "test1", d, "12.jpeg", 1000L, null, null, null);
        Report report2 = createTestReport(2L, "test2", null, null, 1050L, null, null, null);

        List<Report> reports = new ArrayList<>();
        reports.add(report1);
        reports.add(report2);

        return reports;
    }

    private Report createTestReport(Long id, String petReport, LocalDateTime date, String filePath, Long fileSize, String mediaType, byte[] photo, Pet petId) {
        Report report = new Report();
        report.setId(id);
        report.setPetReport(petReport);
        report.setDate(date);
        report.setFilePath(filePath);
        report.setFileSize(fileSize);
        report.setMediaType(mediaType);
        report.setPhoto(photo);
        report.setPet(petId);
        return report;
    }

}
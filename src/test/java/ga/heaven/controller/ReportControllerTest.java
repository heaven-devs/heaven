package ga.heaven.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import ga.heaven.model.Breed;
import ga.heaven.model.Pet;
import ga.heaven.model.Report;
import ga.heaven.repository.ReportRepository;
import ga.heaven.service.ReportService;
import org.assertj.core.api.Assertions;
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


    private List<Report> reportsForTest() {

        LocalDateTime d = LocalDate.of(2023, 3, 5).atStartOfDay();
        Report report1 = createTestReport(1L, "test1", d, "12.jpeg", 1000L, null, null, null);
        Report report2 = createTestReport(2L, "test2", null, null, 1050L, null, null, null);

        List<Report> reports = new ArrayList<>();
        reports.add(report1);
        reports.add(report2);

        return reports;
    }

    private Report createTestReport(long id, String petReport, LocalDateTime date, String filePath, Long fileSize, String mediaType, byte[] photo, Pet petId) {
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
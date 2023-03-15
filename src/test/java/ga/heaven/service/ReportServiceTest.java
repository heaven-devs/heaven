package ga.heaven.service;

import ga.heaven.model.Pet;
import ga.heaven.model.Report;
import ga.heaven.repository.ReportRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private ReportService reportService;


    private List<Report> listReportsTest() {
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
        return (report);
    }

    private Report testReport() {
        Report r1 = new Report();
        r1.setId(1L);
        r1.setPetReport("report");
        r1.setDate(LocalDate.of(2023, 6, 7).atStartOfDay());
        r1.setFilePath(null);
        r1.setFileSize(750L);
        r1.setMediaType("12345");
        r1.setPhoto(null);
        r1.setPet(null);
        return r1;

    }

    private Report testReportWrong() {
        Report r2 = new Report();
        r2.setId(5L);
        r2.setPetReport("report2");
        r2.setDate(LocalDate.of(2023, 3, 5).atStartOfDay());
        r2.setFilePath("file");
        r2.setFileSize(750L);
        r2.setMediaType("12345");
        r2.setPhoto(null);
        r2.setPet(null);
        return r2;
    }


    @Test
    public void findAllReportsTest() {
        List<Report> reports = listReportsTest();
        when(reportRepository.findAll()).thenReturn(reports);
        List<Report> actual = reportService.findAllReports();
        Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(reports);
    }

    @Test
    public void findReportsById() {
        when(reportRepository.findById(1L)).thenReturn(Optional.of(testReport()));
        when(reportRepository.findById(5L)).thenReturn(Optional.of(testReportWrong()));

        Report actual = reportService.findReportsById(1L);

        Assertions.assertThat(actual.getId()).isEqualTo(testReport().getId());
        Assertions.assertThat(actual.getPetReport()).isEqualTo(testReport().getPetReport());
        Assertions.assertThat(actual.getPhoto()).isEqualTo(testReport().getPhoto());
        Assertions.assertThat(actual.getDate()).isEqualTo(testReport().getDate());

        Assertions.assertThat(reportService.findReportsById(5L).getId()).isNotEqualTo(testReport().getId());
    }

    @Test
    public void deleteReport() {
        when(reportRepository.findById(2L)).thenReturn(Optional.empty());
        Report expected = null;
        Report actual = reportService.deleteReport(2L);
        Assertions.assertThat(actual).isEqualTo(expected);
        Report report = createTestReport(1L, "test1", LocalDate.of(2023, 3, 5).atStartOfDay(), "12.jpeg", 1000L, null, null, null);
        when(reportRepository.findById(1L)).thenReturn(Optional.of(report));

        expected = report;
        actual = reportService.deleteReport(1L);
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void  createReport(){
        when(reportRepository.save(testReport())).thenReturn(testReport());
        assertThat(reportService.createReport(testReport())).isEqualTo(testReport());
    }

    @Test
    public void updateReport(){
        Report expected = createTestReport(1L, "test1", LocalDate.of(2023, 3, 5).atStartOfDay(), "12.jpeg", 1000L, null, null, null);
        when(reportRepository.findById(expected.getId())).thenReturn(Optional.ofNullable(expected));
        when(reportRepository.save(expected)).thenReturn(expected);
        Report actual = reportService.updateReport(expected);
        assertThat(actual).isEqualTo(expected);
    }

}

package ga.heaven.service;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.model.Update;
import ga.heaven.listener.TelegramBotUpdatesListenerTest;
import ga.heaven.model.Customer;
import ga.heaven.model.CustomerContext;
import ga.heaven.model.Pet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static ga.heaven.configuration.Constants.REPORT_SUBMIT_CMD;
import static ga.heaven.configuration.ReportConstants.*;
import static ga.heaven.model.CustomerContext.Context.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReportSelectorServiceTest {

    @InjectMocks
    private ReportSelectorService reportSelectorService;

    @Mock
    private MsgService msgService;
    @Mock
    private ReportService reportService;
    @Mock
    private PetService petService;
    @Mock
    private CustomerService customerService;
    @Mock
    private CustomerContextService customerContextService;

    private static final Logger logger = LoggerFactory.getLogger(ReportSelectorServiceTest.class);

    private String updateResourceFile = "text_update.json";
    private String updateResourceFileWithPhoto = "text_update_with_photo.json";
    private String updateResourceFileWithPhotoAndCaption = "text_update_with_photo_and_caption.json";
    private Customer expectedCustomer;
    private CustomerContext expectedCustomerContext;
    private List<Pet> expectedNonePetsOfCustomer;
    private List<Pet> expectedOnePetOfCustomer;
    private List<Pet> expectedTwoPetsOfCustomer;
    private Update update;


    @BeforeEach
    private void initialTest() {
        expectedCustomer = new Customer(1L, 777_777_777L, "surname", "name", "secondName", "phone", "address", null);
        expectedCustomerContext = new CustomerContext(1L, FREE, 1L, expectedCustomer);
        expectedCustomer.setCustomerContext(expectedCustomerContext);
        expectedNonePetsOfCustomer = new ArrayList<>();
        expectedOnePetOfCustomer = new ArrayList<>(expectedNonePetsOfCustomer);
        expectedOnePetOfCustomer.add(new Pet(1L, expectedCustomer));
        expectedOnePetOfCustomer.get(0).setName("Pet1");
        expectedTwoPetsOfCustomer = new ArrayList<>(expectedOnePetOfCustomer);
        expectedTwoPetsOfCustomer.add(new Pet(4L, expectedCustomer));
        expectedTwoPetsOfCustomer.get(1).setName("Pet2");

    }

    @Test
    public void switchCmdTestForSubmitReportCustomerWithoutPets() {
        update = getUpdateFromResourceFile(updateResourceFile, REPORT_SUBMIT_CMD);
        when(customerService.findCustomerByChatId(update.message().chat().id())).thenReturn(expectedCustomer);
        when(petService.findPetsByCustomerOrderById(expectedCustomer)).thenReturn(expectedNonePetsOfCustomer);

        reportSelectorService.switchCmd(update.message());

        ArgumentCaptor<Long> argumentCaptor1 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> argumentCaptor2 = ArgumentCaptor.forClass(String.class);
        Mockito.verify(msgService).sendMsg(argumentCaptor1.capture(), argumentCaptor2.capture());
        Long actual1 = argumentCaptor1.getValue();
        String actual2 = argumentCaptor2.getValue();

        assertThat(actual1).isEqualTo(update.message().chat().id());
        assertThat(actual2).isEqualTo(ANSWER_DONT_HAVE_PETS);
    }

    @Test
    public void switchCmdTestForSubmitReportCustomerWithOnePet() {
        update = getUpdateFromResourceFile(updateResourceFile, REPORT_SUBMIT_CMD);
        when(customerService.findCustomerByChatId(update.message().chat().id())).thenReturn(expectedCustomer);
        when(petService.findPetsByCustomerOrderById(expectedCustomer)).thenReturn(expectedOnePetOfCustomer);
        when(customerContextService.findCustomerContextByCustomer(expectedCustomer)).thenReturn(expectedCustomerContext);

        reportSelectorService.switchCmd(update.message());

        ArgumentCaptor<Long> argumentCaptor1 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> argumentCaptor2 = ArgumentCaptor.forClass(String.class);
        Mockito.verify(msgService).sendMsg(argumentCaptor1.capture(), argumentCaptor2.capture());
        Long actual1 = argumentCaptor1.getValue();
        String actual2 = argumentCaptor2.getValue();

        assertThat(actual1).isEqualTo(update.message().chat().id());
        assertThat(actual2).isEqualTo(ANSWER_ONE_PET);
    }

    @Test
    public void switchCmdTestForSubmitReportCustomerWithTwoPets() {
        update = getUpdateFromResourceFile(updateResourceFile, REPORT_SUBMIT_CMD);
        when(customerService.findCustomerByChatId(update.message().chat().id())).thenReturn(expectedCustomer);

        String expectedAnswerTwoPets = "?????????????? id ??????????????:" + "1. Pet1" + "4. Pet2";
        expectedAnswerTwoPets = expectedAnswerTwoPets.replace(" ", "");

        when(petService.findPetsByCustomerOrderById(expectedCustomer)).thenReturn(expectedTwoPetsOfCustomer);
        when(customerContextService.findCustomerContextByCustomer(expectedCustomer)).thenReturn(expectedCustomerContext);

        reportSelectorService.switchCmd(update.message());

        ArgumentCaptor<Long> argumentCaptor1 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> argumentCaptor2 = ArgumentCaptor.forClass(String.class);
        Mockito.verify(msgService).sendMsg(argumentCaptor1.capture(), argumentCaptor2.capture());
        Long actual1 = argumentCaptor1.getValue();
        String actual2 = argumentCaptor2.getValue().replace("\r\n", "").replace(" ", "");

        assertThat(actual1).isEqualTo(update.message().chat().id());
        assertThat(actual2).isEqualTo(expectedAnswerTwoPets);
    }

    @Test
    public void SwitchCmdTestForContextWaitPetIdInvalidPetNumber() {
        update = getUpdateFromResourceFile(updateResourceFile, "Text report from customer");
        expectedCustomerContext.setDialogContext(WAIT_PET_ID);

        when(customerService.findCustomerByChatId(update.message().chat().id())).thenReturn(expectedCustomer);
        when(customerContextService.findById(expectedCustomer.getId())).thenReturn(expectedCustomerContext);
        when(petService.findPetsByCustomerOrderById(expectedCustomer)).thenReturn(expectedTwoPetsOfCustomer);

        reportSelectorService.switchCmd(update.message());

        ArgumentCaptor<Long> argumentCatpor1 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> argumentCaptor2 = ArgumentCaptor.forClass(String.class);
        Mockito.verify(msgService).sendMsg(argumentCatpor1.capture(), argumentCaptor2.capture());
        Long actual1 = argumentCatpor1.getValue();
        String actual2 = argumentCaptor2.getValue();

        assertThat(actual1).isEqualTo(expectedCustomer.getChatId());
        assertThat(actual2).isEqualTo(ANSWER_NON_EXISTENT_PET);
    }

    @Test
    public void SwitchCmdTestForContextWaitPetIdValidPetNumber() {
        String expectedPetId = expectedTwoPetsOfCustomer.get(0).getId().toString();
        update = getUpdateFromResourceFile(updateResourceFile, expectedPetId);
        expectedCustomerContext.setDialogContext(WAIT_PET_ID);

        when(customerService.findCustomerByChatId(update.message().chat().id())).thenReturn(expectedCustomer);
        when(customerContextService.findById(expectedCustomer.getId())).thenReturn(expectedCustomerContext);
        when(customerContextService.findCustomerContextByCustomer(expectedCustomer)).thenReturn(expectedCustomerContext);
        when(petService.findPetsByCustomerOrderById(expectedCustomer)).thenReturn(expectedTwoPetsOfCustomer);

        reportSelectorService.switchCmd(update.message());

        ArgumentCaptor<Long> argumentCatpor1 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> argumentCaptor2 = ArgumentCaptor.forClass(String.class);
        Mockito.verify(msgService).sendMsg(argumentCatpor1.capture(), argumentCaptor2.capture());
        Long actual1 = argumentCatpor1.getValue();
        String actual2 = argumentCaptor2.getValue();

        assertThat(actual1).isEqualTo(expectedCustomer.getChatId());
        assertThat(actual2).isEqualTo(ANSWER_SEND_REPORT_FOR_PET_WITH_ID + expectedPetId);
    }

    @Test
    public void SwitchCmdTestForContextWaitReportGetTextReport() {
        update = getUpdateFromResourceFile(updateResourceFile, "?????????? ????????????, ?????? ????????????????");
        expectedCustomerContext.setDialogContext(WAIT_REPORT);

        when(customerService.findCustomerByChatId(update.message().chat().id())).thenReturn(expectedCustomer);
        when(customerContextService.findById(expectedCustomer.getId())).thenReturn(expectedCustomerContext);
        when(customerContextService.findCustomerContextByCustomer(expectedCustomer)).thenReturn(expectedCustomerContext);

        reportSelectorService.switchCmd(update.message());

        ArgumentCaptor<Long> argumentCatpor1 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> argumentCaptor2 = ArgumentCaptor.forClass(String.class);
        Mockito.verify(msgService).sendMsg(argumentCatpor1.capture(), argumentCaptor2.capture());
        Long actual1 = argumentCatpor1.getValue();
        String actual2 = argumentCaptor2.getValue();

        assertThat(actual1).isEqualTo(expectedCustomer.getChatId());
        assertThat(actual2).isEqualTo(ANSWER_REPORT_NOT_ACCEPTED_PHOTO_REQIRED);
    }

    @Test
    public void SwitchCmdTestForContextWaitReportGetPhotoReport() {
        String expectedPhoto = "???????? ??????????, ?????? ????????????";
        update = getUpdateFromResourceFile(updateResourceFileWithPhoto, expectedPhoto);
        expectedCustomerContext.setDialogContext(WAIT_REPORT);

        when(customerService.findCustomerByChatId(update.message().chat().id())).thenReturn(expectedCustomer);
        when(customerContextService.findById(expectedCustomer.getId())).thenReturn(expectedCustomerContext);
        when(customerContextService.findCustomerContextByCustomer(expectedCustomer)).thenReturn(expectedCustomerContext);

        reportSelectorService.switchCmd(update.message());

        ArgumentCaptor<Long> argumentCatpor1 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> argumentCaptor2 = ArgumentCaptor.forClass(String.class);
        Mockito.verify(msgService).sendMsg(argumentCatpor1.capture(), argumentCaptor2.capture());
        Long actual1 = argumentCatpor1.getValue();
        String actual2 = argumentCaptor2.getValue();

        assertThat(actual1).isEqualTo(expectedCustomer.getChatId());
        assertThat(actual2).isEqualTo(ANSWER_REPORT_NOT_ACCEPTED_DESCRIPTION_REQUIRED);
    }

    @Test
    public void SwitchCmdTestForContextWaitReportGetFullReport() {
        String expectedCaption = "???????????? ??????????: ???????? ?? ??????????";
        update = getUpdateFromResourceFile(updateResourceFileWithPhotoAndCaption, expectedCaption);
        expectedCustomerContext.setDialogContext(WAIT_REPORT);

        when(customerService.findCustomerByChatId(update.message().chat().id())).thenReturn(expectedCustomer);
        when(customerContextService.findById(expectedCustomer.getId())).thenReturn(expectedCustomerContext);
        when(customerContextService.findCustomerContextByCustomer(expectedCustomer)).thenReturn(expectedCustomerContext);

        reportSelectorService.switchCmd(update.message());

        ArgumentCaptor<Long> argumentCatpor1 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> argumentCaptor2 = ArgumentCaptor.forClass(String.class);
        Mockito.verify(msgService).sendMsg(argumentCatpor1.capture(), argumentCaptor2.capture());
        Long actual1 = argumentCatpor1.getValue();
        String actual2 = argumentCaptor2.getValue();

        assertThat(actual1).isEqualTo(expectedCustomer.getChatId());
        assertThat(actual2).isEqualTo(ANSWER_REPORT_ACCEPTED);
    }


    private Update getUpdateFromResourceFile(String resourceFile, String command) {
        String json = getJson(resourceFile);
        return BotUtils.fromJson(json.replace("%command%", command), Update.class);
    }

    private String getJson(String resourceFile) {
        String json = null;
        try {
            json = Files.readString(
                    Paths.get(TelegramBotUpdatesListenerTest.class.getResource(resourceFile).toURI()));
        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (URISyntaxException e) {
            logger.error(e.getMessage());
        }
        return json;
    }

}

package ga.heaven.service;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import ga.heaven.listener.TelegramBotUpdatesListenerTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static ga.heaven.configuration.Constants.START_CMD;
import static ga.heaven.configuration.Constants.VOLUNTEER_REQUEST_CMD;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CmdSelectorServiceTest {

    @Mock
    private AppLogicService appLogicService;
    @Mock
    private PetSelectorService petSelectorService;
    @Mock
    private ReportSelectorService reportSelectorService;
    @Mock
    private VolunteerSelectorService volunteerSelectorService;
    
    static final Integer NUMBER_OF_INVOCATIONS = 1;
    @InjectMocks
    CmdSelectorService cmdSelectorService;

    @Test
    public void checkInitConversationParameter() throws URISyntaxException, IOException {
        String json = Files.readString(
                Paths.get(TelegramBotUpdatesListenerTest.class.getResource("text_update.json").toURI()));
        Update update = getUpdate(json, START_CMD);
        cmdSelectorService.processingMsg(update.message());

        Long expectedChatId = 777_777_777L;

        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(appLogicService).initConversation(argumentCaptor.capture());
        Long actual = argumentCaptor.getValue();
        assertThat(actual).isEqualTo(expectedChatId);
    }

    @Test
    public void checkVolunteerRequestParameter() throws URISyntaxException, IOException {
        String json = Files.readString(
                Paths.get(TelegramBotUpdatesListenerTest.class.getResource("text_update.json").toURI()));
        Update update = getUpdate(json, "any text");
        volunteerSelectorService.switchCmd(update.message());

        ArgumentCaptor<Message> argumentCaptor = ArgumentCaptor.forClass(Message.class);
        Mockito.verify(volunteerSelectorService).switchCmd(argumentCaptor.capture());
        Message actual = argumentCaptor.getValue();

        assertThat(actual.text()).isEqualTo("any text");
        assertThat(actual.chat().id()).isEqualTo(777_777_777L);
        verify(volunteerSelectorService, times(NUMBER_OF_INVOCATIONS)).switchCmd(actual);
    }

    private Update getUpdate(String json, String replaced) {
        return BotUtils.fromJson(json.replace("%command%", replaced), Update.class);
    }
}

package ga.heaven.listener;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import ga.heaven.service.CmdSelectorService;
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
import java.util.Collections;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TelegramBotUpdatesListenerTest {

    @Mock
    private TelegramBot telegramBot;
    @Mock
    private CmdSelectorService cmdSelectorService;

    @InjectMocks
    private TelegramBotUpdatesListener telegramBotUpdatesListener;

    @Test
    public void handleNullInMessage() throws URISyntaxException, IOException {
        String json = Files.readString(
                Paths.get(TelegramBotUpdatesListenerTest.class.getResource("text_with_null.json").toURI()));
        Update update = getUpdate(json, "/start");
        telegramBotUpdatesListener.process(Collections.singletonList(update));

        ArgumentCaptor<Message> argumentCaptor = ArgumentCaptor.forClass(Message.class);
        Mockito.verify(cmdSelectorService).processingMsg(argumentCaptor.capture());
        Message actual = argumentCaptor.getValue();
        assertThat(actual.messageId()).isNull();
    }

    private Update getUpdate(String json, String replaced) {
        return BotUtils.fromJson(json.replace("%command%", replaced), Update.class);
    }
}

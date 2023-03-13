package ga.heaven.service;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import ga.heaven.listener.TelegramBotUpdatesListenerTest;
import ga.heaven.model.Customer;
import ga.heaven.model.CustomerContext;
import org.assertj.core.api.Assertions;
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
import java.nio.file.Files;
import java.nio.file.Paths;

@ExtendWith(MockitoExtension.class)
public class MsgServiceTest {

    @InjectMocks
    private MsgService msgService;

    @Mock
    private TelegramBot telegramBot;

    private static final Logger logger = LoggerFactory.getLogger(MsgServiceTest.class);
    private Long expectedChatId;
    private Customer expectedCustomer;

    @BeforeEach
    private void initialTest() {
        expectedChatId = 777_777_777L;
        expectedCustomer = new Customer();
        expectedCustomer.setId(1L);
        expectedCustomer.setChatId(expectedChatId);
        expectedCustomer.setName("Ivan");
        expectedCustomer.setCustomerContext(new CustomerContext(1L, CustomerContext.Context.FREE, 2L, 1L));
    }

    @Test
    public void sendMessageTest() {
        String expectedCommand = "/start";

        Update update = getUpdateFromResourceFile("text_update.json", expectedCommand);
        msgService.sendMsg(update.message().chat().id(), update.message().text());

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(expectedChatId);
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(expectedCommand);
        Assertions.assertThat(actual.getParameters().get("parse_mode")).isEqualTo(ParseMode.HTML.name());
    }

    @Test
    public void sendMessageTestWithKeyboard() {
        String expectedCommand = "/start";
        Keyboard expectedKeyboard = new ReplyKeyboardMarkup(expectedCommand);

        Update update = getUpdateFromResourceFile("text_update.json", expectedCommand);
        msgService.sendMsg(update.message().chat().id(), update.message().text(), expectedKeyboard);

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();
        actual.replyMarkup(new ReplyKeyboardMarkup(update.message().text()));

        Assertions.assertThat(actual.getParameters().get("reply_markup")).isNotNull();
        Assertions.assertThat(actual.getParameters().get("reply_markup").getClass()).isEqualTo(ReplyKeyboardMarkup.class);
    }

   /* @Test
    public void shouldReturnKeyboard() {
        ReplyKeyboardMarkup actual = msgService.selectShelter();
        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getClass()).isEqualTo(ReplyKeyboardMarkup.class);
    }*/

    private Update getUpdateFromResourceFile(String jsonFile, String command) {
        String json = null;
        try {
            json = Files.readString(
                    Paths.get(TelegramBotUpdatesListenerTest.class.getResource(jsonFile).toURI()));
        } catch (IOException | URISyntaxException e) {
            logger.error(e.getMessage());
        }
        return BotUtils.fromJson(json.replace("%command%", command), Update.class);
    }

}

package ga.heaven.service;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.Keyboard;
import ga.heaven.listener.TelegramBotUpdatesListenerTest;
import ga.heaven.model.Customer;
import ga.heaven.model.CustomerContext;
import ga.heaven.model.Info;
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

import static ga.heaven.configuration.Constants.*;
import static ga.heaven.model.CustomerContext.Context.FREE;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AppLogicServiceTest {

    @InjectMocks
    private AppLogicService appLogicService;

    @Mock
    private InfoService infoService;
    @Mock
    private CustomerService customerService;
    @Mock
    private ShelterService shelterService;
    @Mock
    private MsgService msgService;

    private Info expectedInfo;
    private Long expectedChatId;
    private Customer expectedCustomer;

    private static final Logger logger = LoggerFactory.getLogger(AppLogicServiceTest.class);

    @BeforeEach
    private void getInitialTestBreeds() {
        expectedChatId = 777_777_777L;
        expectedInfo = new Info(1L, "area", COMMON_INFO_FIELD);
        expectedCustomer = new Customer();
        expectedCustomer.setId(1L);
        expectedCustomer.setChatId(expectedChatId);
        expectedCustomer.setName("Ivan");
        expectedCustomer.setCustomerContext(new CustomerContext(1L, FREE, 2L, 1L));
    }

    @Test
    public void handleBotCommandDatingRulesPositive() {
        when(infoService.findInfoByArea(DATING_RULES_FIELD)).thenReturn(expectedInfo);

        Update update = getUpdateFromResourceFile("text_update.json", DATING_RULES_CMD);
        appLogicService.sendDatingRules(update.message().chat().id());

        ArgumentCaptor<Long> argumentCaptor1 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> argumentCaptor2 = ArgumentCaptor.forClass(String.class);
        Mockito.verify(msgService).sendMsg(argumentCaptor1.capture(), argumentCaptor2.capture());
        Long actualArgument1 = argumentCaptor1.getValue();
        String actualArgument2 = argumentCaptor2.getValue();

        Assertions.assertThat(actualArgument1).isEqualTo(expectedChatId);
        Assertions.assertThat(actualArgument2).isEqualTo(expectedInfo.getInstructions());
    }

    @Test
    public void handleBotCommandDatingRulesNegative() {
        expectedInfo = null;

        when(infoService.findInfoByArea(DATING_RULES_FIELD)).thenReturn(expectedInfo);

        Update update = getUpdateFromResourceFile("text_update.json", DATING_RULES_CMD);
        appLogicService.sendDatingRules(update.message().chat().id());

        ArgumentCaptor<Long> argumentCaptor1 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> argumentCaptor2 = ArgumentCaptor.forClass(String.class);
        Mockito.verify(msgService).sendMsg(argumentCaptor1.capture(), argumentCaptor2.capture());
        Long actualArgument1 = argumentCaptor1.getValue();
        String actualArgument2 = argumentCaptor2.getValue();

        Assertions.assertThat(actualArgument1).isEqualTo(expectedChatId);
        Assertions.assertThat(actualArgument2).isEqualTo(DATING_RULES_NOT_FOUND);
    }

    @Test
    public void handleInitConversationForNewCustomer() {
        String expectedCommand = COMMON_INFO_FIELD;
        when(customerService.isPresent(expectedChatId)).thenReturn(false);
        when(infoService.findInfoByArea(expectedCommand)).thenReturn(expectedInfo);
        when(customerService.createCustomer(expectedChatId)).thenReturn(expectedCustomer);

        Update update = getUpdateFromResourceFile("text_update.json", expectedCommand);
        appLogicService.initConversation(update.message().chat().id());

        ArgumentCaptor<Long> argumentCaptor1 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> argumentCaptor2 = ArgumentCaptor.forClass(String.class);
        Mockito.verify(msgService).sendMsg(argumentCaptor1.capture(), argumentCaptor2.capture());
        Long actualArgument1 = argumentCaptor1.getValue();
        String actualArgument2 = argumentCaptor2.getValue();
        Assertions.assertThat(actualArgument1).isEqualTo(expectedChatId);
        Assertions.assertThat(actualArgument2).isEqualTo(expectedCommand);
    }

    @Test
    public void handleInitConversationForExistingUser() {
        InlineKeyboardMarkup expectedKbMarkup = new InlineKeyboardMarkup();

        when(customerService.isPresent(expectedChatId)).thenReturn(true);

        Update update = getUpdateFromResourceFile("text_update.json", COMMON_INFO_FIELD);
        appLogicService.initConversation(update.message().chat().id());

        ArgumentCaptor<Long> argumentCaptor1 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> argumentCaptor2 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Keyboard> argumentCaptor3 = ArgumentCaptor.forClass(Keyboard.class);

        Mockito.verify(msgService).sendMsg(argumentCaptor1.capture(), argumentCaptor2.capture(), argumentCaptor3.capture());

        Long actualArgument1 = argumentCaptor1.getValue();
        String actualArgument2 = argumentCaptor2.getValue();
        Keyboard actualArgument3 = argumentCaptor3.getValue();

        Assertions.assertThat(actualArgument1).isEqualTo(expectedChatId);
        Assertions.assertThat(actualArgument2).isEqualTo(SHELTER_CHOOSE_MSG);
        Assertions.assertThat(actualArgument3).isEqualTo(expectedKbMarkup);
    }

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

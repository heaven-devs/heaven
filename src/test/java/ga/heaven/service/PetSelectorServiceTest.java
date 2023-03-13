package ga.heaven.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.model.Update;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URISyntaxException;

import static ga.heaven.configuration.Constants.DATING_RULES_CMD;
import static ga.heaven.service.PeripheralService.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PetSelectorServiceTest {
    @InjectMocks
    PetSelectorService petSelectorService;
    
    @Mock
    private AppLogicService appLogicService;
    
    @Mock
    CmdSelectorService cmdSelectorService;
    
    static final Integer NUMBER_OF_INVOCATIONS = 1;
    static final Long FROM_ID = 198787L;
    static final Long CHAT_ID = 984759475L;
    
    /*private Update createUpdate() throws JSONException, URISyntaxException, IOException {
        return createUpdate(null, null, null);
    }*/
    
    /*private Update createUpdate(Long fromId, Long chatId, String msgText) throws URISyntaxException, IOException, JSONException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = (ObjectNode) mapper.createObjectNode();
        ObjectNode messageNode = rootNode.putObject("message");
        if (fromId != null) {
            messageNode.putObject("from").put("id", fromId);
        }
        if (chatId != null) {
            messageNode.putObject("chat").put("id", chatId);
        }
        if (msgText != null) {
            messageNode.put("text", msgText);
        }
        return BotUtils.fromJson(rootNode.toPrettyString(), Update.class);
    }*/
    
    @Test
    public void switchCmdTest() throws URISyntaxException, IOException, JSONException {
        Update update = createUpdate(CHAT_ID, DATING_RULES_CMD);
        petSelectorService.switchCmd(update.message());
        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(appLogicService).sendDatingRules(argumentCaptor.capture());
        Long actualChatID = argumentCaptor.getValue();
        Long expectedChatId = CHAT_ID;
        assertThat(actualChatID).isEqualTo(expectedChatId);
        verify(appLogicService, times(NUMBER_OF_INVOCATIONS)).sendDatingRules(actualChatID);
        
    }
    
    
}
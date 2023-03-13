package ga.heaven.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.model.Update;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;

@Service
public class PeripheralService {
    
    private final Logger LOGGER = LoggerFactory.getLogger(PeripheralService.class);
    
    public PeripheralService() {
    
    }
    
    public String index() {
        return "frontend will be here soon";
    }
    
    
    private static Update createUpdate() throws JSONException, URISyntaxException, IOException {
        return createUpdate(null, null, null);
    }
    
    public static Update createUpdate(Long chatId, String msgText) throws URISyntaxException, IOException, JSONException {
        return createUpdate(null, chatId, msgText);
    }
    
    public static Update createUpdate(Long fromId, Long chatId, String msgText) throws URISyntaxException, IOException, JSONException {
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
    }
}

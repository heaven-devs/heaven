package ga.heaven.service;

import com.pengrad.telegrambot.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static ga.heaven.configuration.Constants.*;

@Service
public class PetSelectorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PetSelectorService.class);
    private final AppLogicService appLogicService;
    
    private final MsgService msgService;
    
    public PetSelectorService(AppLogicService appLogicService, MsgService msgService) {
        this.appLogicService = appLogicService;
        this.msgService = msgService;
    }
    
    public void switchCmd(Message inputMessage) {
        this.switchCmd(inputMessage.chat().id(), inputMessage.text());
    }
    
    public void switchCmd(Long chatId, String Command) {
        
        switch (Command) {
            case DATING_RULES_CMD:
                appLogicService.sendDatingRules(chatId);
                break;
            case DOCUMENTS_CMD:
                appLogicService.sendDocuments(chatId);
                break;
            case TRANSPORT_CMD:
                appLogicService.sendTransportRules(chatId);
                break;
            case COMFORT_PET_CMD:
                appLogicService.sendComfortPet(chatId);
                break;
            case COMFORT_DOG_CMD:
                appLogicService.sendComfortDog(chatId);
                break;
            case COMFORT_HANDICAPPED_CMD:
                appLogicService.sendComfortHandicapped(chatId);
                break;
            case CYNOLOGIST_ADVICE_CMD:
                appLogicService.sendCynologistAdvice(chatId);
                break;
            case CYNOLOGISTS_LIST_CMD:
                appLogicService.sendCynologistsList(chatId);
                break;
            case REASONS_REFUSAL_CMD:
                appLogicService.sendReasonsRefusal(chatId);
                break;
        }
    }
    
}

package ga.heaven.service;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import ga.heaven.model.*;
import ga.heaven.repository.ShelterRepository;
import ga.heaven.repository.VolunteerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import static ga.heaven.configuration.Constants.*;

@Service
public class AppLogicService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppLogicService.class);
    
    private final InfoService infoService;
    private final CustomerService customerService;
    private final MsgService msgService;
    private final VolunteerRepository volunteerRepository;
    private final ShelterRepository shelterRepository;
    private final ShelterService shelterService;

    public AppLogicService(InfoService infoService, CustomerService customerService, MsgService msgService, VolunteerRepository volunteerRepository, ShelterRepository shelterRepository, ShelterService shelterService) {
        this.infoService = infoService;
        this.customerService = customerService;
        this.msgService = msgService;
        this.volunteerRepository = volunteerRepository;
        this.shelterRepository = shelterRepository;
        this.shelterService = shelterService;
    }
    
    /*public void sendDatingRules(Long chatId) {
        Info info = infoService.findInfoByArea(DATING_RULES_FIELD);
        if (info == null) {
            msgService.sendMsg(chatId, DATING_RULES_NOT_FOUND);
        } else {
            msgService.sendMsg(chatId, info.getInstructions());
        }
    }*/
    
    public void initConversation(Long chatId) {
        if (!customerService.isPresent(chatId)) {
            msgService.sendMsg(chatId, infoService.findInfoByArea(COMMON_INFO_FIELD).getInstructions());
            customerService.createCustomer(chatId);
        }

        /* ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(
                SHELTER1_CMD, SHELTER2_CMD)
                .resizeKeyboard(true)
                .selective(true); */
        InlineKeyboardMarkup kbMarkup = new InlineKeyboardMarkup();
        shelterService.findAll().forEach(shelter -> {
            kbMarkup.addRow(new InlineKeyboardButton(shelter.getName()).callbackData("/shelter/"+shelter.getId()));
        });
        msgService.sendMsg(chatId, SHELTER_CHOOSE_MSG, kbMarkup);
    }

    public void volunteerRequest(Long chatId) {
        //msgService.sendMsg(inputMessage.chat().id(), "ok");
//        volunteerRepository.findById(3L).ifPresent(volunteer -> msgService.sendMsg(inputMessage.chat().id(), volunteer.getShelters().toString()));
        /*msgService.sendMsg(inputMessage.chat().id(),shelterRepository.findById(1L).ifPresent(shelter -> shelter.getVolunteers().forEach(v -> v.getName())));*/
        Shelter s = shelterRepository.findById(1L).orElse(null);
        msgService.sendMsg(chatId, s.getVolunteers().stream()
                .map(v -> v.getName())
                .collect(Collectors.toList()).toString());

    }

    public void sendDatingRules(Long chatId) {
        sendMultipurpose(chatId, DATING_RULES_FIELD, DATING_RULES_NOT_FOUND);
    }

    public void sendDocuments(Long chatId) {
        sendMultipurpose(chatId, DOCUMENTS_FIELD, DOCUMENTS_NOT_FOUND);
    }

    public void sendTransportRules(Long chatId) {
        sendMultipurpose(chatId, TRANSPORT_FIELD, TRANSPORT_NOT_FOUND);
    }

    public void sendComfortPet(Long chatId) {
        sendMultipurpose(chatId, COMFORT_PET_FIELD, COMFORT_PET_NOT_FOUND);
    }

    public void sendComfortDog(Long chatId) {
        sendMultipurpose(chatId, COMFORT_DOG_FIELD, COMFORT_DOG_NOT_FOUND);
    }

    public void sendComfortHandicapped(Long chatId) {
        sendMultipurpose(chatId, COMFORT_HANDICAPPED_FIELD, COMFORT_HANDICAPPED_NOT_FOUND);
    }

    public void sendCynologistAdvice(Long chatId) {
        sendMultipurpose(chatId, CYNOLOGIST_ADVICE_FIELD, CYNOLOGIST_ADVICE_NOT_FOUND);
    }

    public void sendCynologistsList(Long chatId) {
        sendMultipurpose(chatId, CYNOLOGISTS_LIST_FIELD, CYNOLOGIST_LIST_NOT_FOUND);
    }

    public void sendReasonsRefusal(Long chatId) {
        sendMultipurpose(chatId, REASONS_REFUSAL_FIELD, REASONS_REFUSAL_NOT_FOUND);
    }


    private void sendMultipurpose(Long chatId, String areaField, String notFoundMsg) {
        Info info = infoService.findInfoByArea(areaField);
        if (info == null) {
            msgService.sendMsg(chatId, notFoundMsg);
        } else {
            msgService.sendMsg(chatId, info.getInstructions());
        }
    }
}

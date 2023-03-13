package ga.heaven.service;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import ga.heaven.model.Customer;
import ga.heaven.model.CustomerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ga.heaven.configuration.Constants.*;

@Service
public class CmdSelectorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CmdSelectorService.class);
    private static final String DYNAMIC_ENDPOTINT_REGEXP = "^/(.*)/(.*)[0-9]*";
    private static final String STATIC_ENDPOINT_REGEXP = "^/([^/]*)$";
    private final MsgService msgService;
    private final AppLogicService appLogicService;
    private final PetSelectorService petSelectorService;
    private final VolunteerSelectorService volunteerSelectorService;
    private final ReportSelectorService reportSelectorService;
    private final ShelterService shelterService;
    private final CustomerService customerService;
    
    public CmdSelectorService(MsgService msgService, AppLogicService appLogicService, PetSelectorService petSelectorService, VolunteerSelectorService volunteerSelectorService, ReportSelectorService reportSelectorService, ShelterService shelterService, CustomerService customerService) {
        this.msgService = msgService;
        this.appLogicService = appLogicService;
        this.petSelectorService = petSelectorService;
        this.volunteerSelectorService = volunteerSelectorService;
        this.reportSelectorService = reportSelectorService;
        this.shelterService = shelterService;
        this.customerService = customerService;
    }
    
    public void processingMsg(Message inputMessage) {
        
        if (inputMessage.text() != null || inputMessage.photo() != null) {
            LOGGER.debug("Message\n{}\nsent to: reportSelectorService.switchCmd", inputMessage);
            reportSelectorService.switchCmd(inputMessage);
        }
        
        if ((inputMessage.text() != null)
                && (inputMessage.chat() != null)
                && (inputMessage.chat().id() != null)
        ) {
            if (Pattern.compile(DYNAMIC_ENDPOTINT_REGEXP).matcher(inputMessage.text()).matches()) {
                LOGGER.debug("Dynamic endpoint message\n{}\nsent to: switchDynCmd methods", inputMessage);
                
            } else if (Pattern.compile(STATIC_ENDPOINT_REGEXP).matcher(inputMessage.text()).matches()) {
                LOGGER.debug("Constant endpoint message\n{}\nsent to: switchCmd methods", inputMessage);
                switch (inputMessage.text()) {
                    
                    case START_CMD:
                        appLogicService.initConversation(inputMessage.chat().id());
                        return;
                    
                    default:
                        break;
                }
                
                petSelectorService.switchCmd(inputMessage);
                volunteerSelectorService.switchCmd(inputMessage);
            }
        }
    }
    
    public void processingCallBackQuery(CallbackQuery cbQuery) {
        msgService.sendCallbackQueryResponse(cbQuery.id());
        
        if ((cbQuery.data() != null)
                && (cbQuery.message() != null)
                && (cbQuery.message().chat() != null)
                && (cbQuery.message().chat().id() != null)
        ) {
            final Matcher matcher = Pattern.compile(DYNAMIC_ENDPOTINT_REGEXP).matcher(cbQuery.data());
            if (matcher.matches()) {
                LOGGER.debug("Dynamic endpoint message\n{}\nsent to: switchDynCmd methods", cbQuery);
                msgService.sendMsg(cbQuery.message().chat().id(),
                        shelterService.findById(Long.valueOf(matcher.group(2))).getName()
                                + " selected."
                );
                msgService.deleteMsg(cbQuery.message().chat().id(), cbQuery.message().messageId());
                Customer customer = customerService.findCustomerByChatId(cbQuery.message().chat().id());
                customer.getCustomerContext().setShelterId(Long.valueOf(matcher.group(2)));
                customerService.updateCustomer(customer);
            } else if (Pattern.compile(STATIC_ENDPOINT_REGEXP).matcher(cbQuery.data()).matches()) {
                LOGGER.debug("Constant endpoint message\n{}\nsent to: switchCmd methods", cbQuery);
                petSelectorService.switchCmd(cbQuery.message().chat().id(), cbQuery.data());
                volunteerSelectorService.switchCmd(cbQuery.message().chat().id(), cbQuery.data());
            }
        }
    }
}
    
    

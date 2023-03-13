package ga.heaven.service;

import com.pengrad.telegrambot.model.Message;
import ga.heaven.model.Customer;
import ga.heaven.model.CustomerContext;
import ga.heaven.model.CustomerContext.Context;
import ga.heaven.model.Pet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static ga.heaven.configuration.Constants.*;
import static ga.heaven.configuration.ReportConstants.*;
import static ga.heaven.model.CustomerContext.Context.*;

@Service
public class ReportSelectorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportSelectorService.class);

    private final MsgService msgService;
    private final ReportService reportService;
    private final CustomerService customerService;
    private final PetService petService;

    private Message inputMessage;
    private Customer customer;
    private String responseText;

    public ReportSelectorService(MsgService msgService, ReportService reportService, CustomerService customerService, PetService petService) {
        this.msgService = msgService;
        this.reportService = reportService;
        this.customerService = customerService;
        this.petService = petService;
    }

    /**
     * метод проверяет были ли вызваны команды по работе с отчетом, или было отправлено сообщение с текстом/фото
     * @param inputMessage сообщение полученное от пользователя
     */
    public void switchCmd(Message inputMessage) {
        this.inputMessage = inputMessage;
        customer = customerService.findCustomerByChatId(inputMessage.chat().id());

        if (customer != null && inputMessage.text() != null
                && inputMessage.text().equals(REPORT_SUBMIT_CMD)) {
            msgService.sendMsg(inputMessage.chat().id(), processingSubmitReport());
        } else if (customer != null && inputMessage.text() == null || !inputMessage.text().startsWith("/") ) {
            msgService.sendMsg(inputMessage.chat().id(), processingUserMessages());
        }
    }

    /**
     * метод запускается, если пользователь отправил команду "/submit_report" и отправляет ответ пользователю
     * в зависимости от того сколько питомцев взял пользователь
     */
    private String processingSubmitReport() {
        // todo: сделать проверку, сдавал ли пользователь сегодня отчет?

        List<Pet> customerPetList = petService.findPetsByCustomerOrderById(customer);
        responseText = ANSWER_DONT_HAVE_PETS;

        if (customerPetList.size() == 1) {
            responseText = ANSWER_ONE_PET;
            updateCustomerContext(WAIT_REPORT, customerPetList.get(0).getId());
        } else if (customerPetList.size() > 1) {
            responseText = generateListOfCustomersPets(customerPetList);
            updateCustomerContext(WAIT_PET_ID, 0);
        }
        return responseText;
    }

    /**
     * Метод обновляет значения полей "context" и "petId"
     * @param context новое значение поля "context"
     * @param petId новое значение поля "petId"
     */
    private void updateCustomerContext(Context context, long petId) {
        CustomerContext customerContext = customer.getCustomerContext();
        customerContext.setCurrentPetId(petId);
        customerService.updateCustomer(customer);
        updateCustomerContext(context);
    }

    /**
     * Метод обновляет значения полей "context"
     * @param context новое значение поля "context"
     */
    private void updateCustomerContext(Context context) {
        CustomerContext customerContext = customer.getCustomerContext();
        customerContext.setDialogContext(context);
        customerService.updateCustomer(customer);
    }

    /**
     * Метод формирует сообщение пользователю, со списком его питомцев
     * @param customerPetList Список питомцев, на поручении у пользователя
     * @return список питомцев
     */
    private String generateListOfCustomersPets(List<Pet> customerPetList) {
        StringBuilder sb = new StringBuilder(ANSWER_ENTER_PET_ID + CARRIAGE_RETURN);
        for (Pet pet : customerPetList) {
            sb.append(pet.getId()).append(". ").append(pet.getName()).append(CARRIAGE_RETURN);
        }
        return sb.toString();
    }

    /**
     * Метод выбирает нужный для запуска метод в зависимости от контекста диалога с пользователем
     * @return текст ответа пользователю
     */
    private String processingUserMessages() {
        responseText = "";
        Context context = customer.getCustomerContext().getDialogContext();
        switch (context) {
            case WAIT_PET_ID: responseText = processingMsgWaitPetId(); break;
            case WAIT_REPORT: responseText = processingMsgWaitReport(); break;
        }
        return responseText;
    }

    /**
     * Метод формирует сообщение пользователю, когда пользователь выбирает для какого животного хочет сдать отчет
     * @return текст ответа пользователю
     */
    private String processingMsgWaitPetId() {
        responseText = ANSWER_NON_EXISTENT_PET;
        List<String> validIdList = petService.findPetsByCustomerOrderById(customer).stream()
                .map(Pet::getId)
                .map(Object::toString)
                .collect(Collectors.toList());

        if (validIdList.contains(inputMessage.text())) {
            responseText = ANSWER_SEND_REPORT_FOR_PET_WITH_ID + inputMessage.text();
            updateCustomerContext(WAIT_REPORT, Long.parseLong(inputMessage.text()));
        }
        return responseText;
    }

    /**
     * Метод формирует ответ пользоваетлю и записывает данные в БД, когда пользователь отправляет отчет
     * @return текст ответа пользователю
     */
    private String processingMsgWaitReport() {

        // todo: реализовать запись данных в таблицу report

        System.out.println("inputMessage.photo() = " + inputMessage.photo());
        if (inputMessage.photo() != null && inputMessage.caption() != null) {
            responseText = ANSWER_REPORT_ACCEPTED;
            updateCustomerContext(FREE, 0);
            savePhotoToDB();

        } else if (inputMessage.photo() != null) {
            responseText = ANSWER_REPORT_NOT_ACCEPTED_DESCRIPTION_REQUIRED;
            updateCustomerContext(WAIT_REPORT);
            savePhotoToDB();

        } else if (inputMessage.text() != null) {
            responseText = ANSWER_REPORT_NOT_ACCEPTED_PHOTO_REQIRED;
            updateCustomerContext(WAIT_REPORT);
        }

        return responseText;
    }

    /**
     * Метод получает фото и записывает его в БД
     */
    private void savePhotoToDB() {
        // todo: получить фото от бота, разложить на байты, записать в базу. https://www.baeldung.com/java-download-file
    }

}

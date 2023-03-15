package ga.heaven.service;

import com.pengrad.telegrambot.model.Message;
import ga.heaven.exception.FileIsTooBigException;
import ga.heaven.exception.PetNotFoundException;
import ga.heaven.model.Customer;
import ga.heaven.model.CustomerContext;
import ga.heaven.model.CustomerContext.Context;
import ga.heaven.model.Pet;
import ga.heaven.repository.PetRepository;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.hibernate.annotations.NotFound;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.webjars.NotFoundException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static ga.heaven.configuration.Constants.*;
import static ga.heaven.configuration.ReportConstants.*;
import static ga.heaven.model.CustomerContext.Context.*;
import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
public class ReportSelectorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportSelectorService.class);

    private final MsgService msgService;
    private final ReportService reportService;
    private final CustomerService customerService;
    private final PetService petService;

    private final int fileSizeLimit = 300;

    private Message inputMessage;
    private Customer customer;
    private String responseText;
    private final PetRepository petRepository;

    public ReportSelectorService(MsgService msgService, ReportService reportService, CustomerService customerService, PetService petService,
                                 PetRepository petRepository) {
        this.msgService = msgService;
        this.reportService = reportService;
        this.customerService = customerService;
        this.petService = petService;
        this.petRepository = petRepository;
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
    private void savePhotoToDB(Long petId, MultipartFile file) throws IOException {
        // todo: получить фото от бота, разложить на байты, записать в базу. https://www.baeldung.com/java-download-file


        if(file.getSize() > 1024 * fileSizeLimit){
            throw new FileIsTooBigException(fileSizeLimit);
        }
        Pet pet = petService.read(petId);

        if(pet == null){
            throw new PetNotFoundException(petId);
        }
        Path filePath =Path.of(String.valueOf(pet),petId + ". " + getExtension(file.getOriginalFilename()));
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);

        try(InputStream is = file.getInputStream();
            OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
            BufferedInputStream bis = new BufferedInputStream(is, 1024);
            BufferedOutputStream bos = new BufferedOutputStream(os, 1024);
        ){
            bis.transferTo(bos);
        }

        Pet photo = findPhoto(petId);
        photo.setPet(petId);
        photo.setFilePath(filePath.toString());
        photo.setFileSize(file.getSize());
        photo.setMediaType(file.getContentType());
        photo.setPhoto(generateImagePreview(filePath));

        petRepository.save(photo);
    }

    private byte[] generateImagePreview(Path filePath) throws IOException {
        try(InputStream is = Files.newInputStream(filePath);
            BufferedInputStream bis = new BufferedInputStream(is, 1024);
            ByteArrayOutputStream bos = new ByteArrayOutputStream()
        ){
            BufferedImage image = ImageIO.read(bis);

            int height = image.getHeight() / (image.getWidth() / 100);
            BufferedImage preview = new BufferedImage(100, height, image.getType());
            Graphics2D graphics = preview.createGraphics();
            graphics.drawImage(image, 0, 0, 100, height, null);
            graphics.dispose();

            ImageIO.write(preview, getExtension(filePath.getFileName().toString()), bos);
            return bos.toByteArray();
        }
    }
    public Pet findPhoto(Long petId) {

        return petRepository.findById(petId).orElse(new Pet());
    }
    private String getExtension(String fileName) {

        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }





}

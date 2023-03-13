package ga.heaven.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.SendResponse;
import ga.heaven.listener.TelegramBotUpdatesListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static ga.heaven.configuration.Constants.SHELTER1_CMD;
import static ga.heaven.configuration.Constants.SHELTER2_CMD;

@Service
public class MsgService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    
    private final TelegramBot telegramBot;

    public MsgService(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

   /* public ReplyKeyboardMarkup selectShelter() {
        LOGGER.info("Shelters keyboard viewed");
        return new ReplyKeyboardMarkup(
                SHELTER1_CMD, SHELTER2_CMD)
                .resizeKeyboard(true)
                .selective(true);
    }*/
    
    public void deleteMsg(Long chatId, Integer msgId) {
        DeleteMessage deleteMessage = new DeleteMessage(chatId, msgId);
        BaseResponse deleteResponse = telegramBot.execute(deleteMessage);
        if (!deleteResponse.isOk()) {
            LOGGER.error(deleteResponse.description());
        }
    }
    
    public BaseResponse sendCallbackQueryResponse(String id) {
        return telegramBot.execute(new AnswerCallbackQuery(id));
    }
    
    public void sendMsg(Long chatId, String inputMessage) {
        sendMsg(chatId, inputMessage, null);
    }
    
    public void sendMsg(Long chatId, String inputMessage, Keyboard keyboard) {
        SendMessage outputMessage = new SendMessage(chatId, inputMessage)
                .parseMode(ParseMode.HTML);
        if (keyboard != null) {
            outputMessage.replyMarkup(keyboard);
        }
        SendResponse sendResponse = telegramBot.execute(outputMessage);
        if (!sendResponse.isOk()) {
            LOGGER.error(sendResponse.description());
        }
    }
}

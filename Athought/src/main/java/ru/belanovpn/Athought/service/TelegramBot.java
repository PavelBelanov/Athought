package ru.belanovpn.Athought.service;

import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.belanovpn.Athought.Repository.AdsRepository;
import ru.belanovpn.Athought.Repository.UserRepository;
import ru.belanovpn.Athought.config.BotConfig;
import ru.belanovpn.Athought.model.User;
import ru.belanovpn.Athought.weatherapi.service.WeatherService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final UserRepository userRepository;
    private final BotConfig config;
    private final WeatherService weatherService;

    private final AdsRepository adsRepository;

    private static final String HELP_TEXT = "This bot was created for nothing. Just click the buttons and have fun!";

    public TelegramBot(BotConfig config, UserRepository userRepository, WeatherService weatherService, AdsRepository adsRepository) {
        this.config = config;
        this.userRepository = userRepository;
        this.weatherService = weatherService;
        this.adsRepository = adsRepository;
        List<BotCommand> listOf = new ArrayList<>();
        listOf.add(new BotCommand("/start", "get a welcome message"));
        listOf.add(new BotCommand("/help", "how to use this bot"));
        listOf.add(new BotCommand("/settings", "set your preferences"));
        listOf.add(new BotCommand("/register", "Registration user"));
        listOf.add(new BotCommand("/weather", "Информация о погоде"));

        try {
            this.execute(new SetMyCommands(listOf, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot`s command list: {}", e.getMessage());
        }

    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String name = update.getMessage().getChat().getFirstName();

            switch (messageText) {
                case "/start" -> {
                    startCommandReceived(chatId, name);
                    registerUser(update.getMessage());
                }
                case "/help" -> prepareAndSendMessage(chatId, HELP_TEXT);
                case "/register" -> register(chatId);
                case "/weather" -> {
                    prepareAndSendMessage(chatId, weatherService.weatherInfo("саратов"));

                }
                default -> prepareAndSendMessage(chatId, "Sorry, I'm too young for this.");
            }
        } else if (update.hasCallbackQuery()) {
            String callBackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            if (callBackData.equals("YES_BUTTON")) {
                String text = "Ok";
                CallBackDataExecution((int) messageId, chatId, text);
            } else if (callBackData.equals("NO_BUTTON")) {
                String text = "Not ok";
                CallBackDataExecution((int) messageId, chatId, text);
            }
        }

    }

    private void CallBackDataExecution(int messageId, long chatId, String text) {
        EditMessageText message = new EditMessageText();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setMessageId(messageId);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void register(long chatId) {
        SendMessage message = new SendMessage(String.valueOf(chatId), "Do you really wont registered?");
        List<InlineKeyboardButton> row = new ArrayList<>();

        var yesButton = new InlineKeyboardButton();
        yesButton.setText("Yes");
        yesButton.setCallbackData("YES_BUTTON");
        row.add(yesButton);
        var noButton = new InlineKeyboardButton();
        noButton.setText("No");
        noButton.setCallbackData("NO_BUTTON");
        row.add(noButton);

        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        rowsInLine.add(row);

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup(rowsInLine);
        message.setReplyMarkup(keyboardMarkup);
        executeMessage(message);
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    private void startCommandReceived(long chatId, String name) {
        String answer = EmojiParser.parseToUnicode
                (String.format("Привет, %s.\n Добро пожаловать!\n Если тебе нужна помощь по работе с ботом\n" +
                        " Жми сюда /help", name)
                        + " :grinning:");
        log.info("Response to user {} sent", name);
        prepareAndSendMessage(chatId, answer);
    }

//    private void sendMessage(long chatId, String textMessage) {
//        SendMessage message = new SendMessage();
//        message.setChatId(String.valueOf(chatId));
//        message.setText(textMessage);
//
//        List<KeyboardRow> keyboardRows = new ArrayList<>();
//        KeyboardRow row = new KeyboardRow();
//        row.add("weather");
//        row.add("random joke");
//        keyboardRows.add(row);
//        row = new KeyboardRow();
//        row.add("register");
//        row.add("check my data");
//        row.add("delete my data");
//        keyboardRows.add(row);
//        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup(keyboardRows);
//        message.setReplyMarkup(keyboardMarkup);
//        executeMessage(message);
//    }

    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred: {}", e.getMessage());
        }
    }

    private void registerUser(Message msg) {
        if (userRepository.findById(msg.getChatId()).isEmpty()) {
            var chatId = msg.getChatId();
            var chat = msg.getChat();
            User user = User.builder()
                    .chatId(chatId)
                    .firstName(chat.getFirstName())
                    .lastName(chat.getLastName())
                    .userName(chat.getUserName())
                    .createdAt(LocalDateTime.now())
                    .build();
            userRepository.saveAndFlush(user);
            log.info("user {} saved", user);
        }
    }

    public void prepareAndSendMessage(long chatId, String textMessage) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textMessage);
        executeMessage(message);
    }

    public void workWithMessage(long chatId) {
    }

//    @Scheduled(cron = "0 * * * * *")
//    private void sendAd() {
//        var getAll = adsRepository.findAll();
//        var users = userRepository.findAll();
//        for (Ads ad : getAll) {
//            for(User user: users){
//                prepareAndSendMessage(user.getChatId(), ad.getAdText());
//            }
//        }
//    }
}

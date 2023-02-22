package ru.belanovpn.Athought.controller;

import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.belanovpn.Athought.config.BotConfig;
import ru.belanovpn.Athought.weatherapi.service.WeatherService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;
    private final WeatherService weatherService;


    private static final String HELP_MESSAGE = EmojiParser.parseToUnicode("При помощи данного бота, вы сможете узнать погоду в любой точке мира!\n" +
            "Все что нужно сделать - это написать боту название города! Удачи" + ":wink:");


    public TelegramBot(BotConfig config,  WeatherService weatherService) {
        this.config = config;
        this.weatherService = weatherService;
        List<BotCommand> listOf = new ArrayList<>();
        listOf.add(new BotCommand("/start", "get a welcome message"));
        listOf.add(new BotCommand("/help", "how to use this bot"));

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
                }
                case "/help" -> prepareAndSendMessage(chatId, HELP_MESSAGE);
                default -> executeMessage(prepareAndSendWeatherOrThrow(chatId, messageText));
            }
        }

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


    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred: {}", e.getMessage());
        }
    }

//    private void registerUser(Message msg) {
//        if (userRepository.findById(msg.getChatId()).isEmpty()) {
//            var chatId = msg.getChatId();
//            var chat = msg.getChat();
//            User user = User.builder()
//                    .chatId(chatId)
//                    .firstName(chat.getFirstName())
//                    .lastName(chat.getLastName())
//                    .userName(chat.getUserName())
//                    .registrationAt(LocalDateTime.now())
//                    .build();
//            userRepository.saveAndFlush(user);
//            log.info("user {} saved", user);
//        }
//    }

    public void prepareAndSendMessage(long chatId, String textMessage) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textMessage);
        executeMessage(message);
    }

    private SendMessage prepareAndSendWeatherOrThrow(long chatId, String cityName) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        try {
            message.setText(weatherService.weatherInfo(cityName));
        } catch (Exception e) {
            message.setText("Хм... Не знаю такого города");
        }
        return message;
    }


}

package ru.belanovpn.Athought.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.belanovpn.Athought.controller.TelegramBot;
@Slf4j
@Component
@RequiredArgsConstructor
public class BotInitializer {


   private final TelegramBot bot;

   @EventListener({ContextRefreshedEvent.class})
    public void init(){
       try {
           TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
           botsApi.registerBot(bot);
       } catch (TelegramApiException e) {
           log.error("Error occurred: {}", e.getMessage());
       }
   }
}

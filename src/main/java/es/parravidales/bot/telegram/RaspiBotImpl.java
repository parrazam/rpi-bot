package es.parravidales.bot.telegram;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * @author victor.parra.vidales
 *
 */
@Component
public class RaspiBotImpl extends TelegramLongPollingBot implements RaspiBot {

	private static final Logger LOGGER = LoggerFactory.getLogger(RaspiBotImpl.class);

	@Value("${bot.telegram.username}")
	private String username;

	@Value("${bot.telegram.token}")
	private String token;
	
	@Value("${bot.telegram.userId}")
	private Integer userId;

	@Override
	public void onUpdateReceived(Update update) {
		LOGGER.debug("Update received: {}", update);
		if(Objects.nonNull(update) && Objects.nonNull(update.getMessage())) {
			Message message = update.getMessage();
			if(userId.equals(message.getFrom().getId())) {
				if(message.hasText()) {
					 SendMessage responseMessage = new SendMessage() // Create a SendMessage object with mandatory fields
				                .setChatId(update.getMessage().getChatId())
				                .setParseMode("Markdown")
				                .setText(buildUnknownActionFor(update.getMessage().getText()));
				        try {
				            execute(responseMessage); // Call method to send the message
				        } catch (TelegramApiException e) {
				            LOGGER.error("Exception throwed while sending response message: {}", e.getMessage());
				        }
				}
			} else {
				LOGGER.warn("Unknown userId received: {}", message.getFrom());
			}
		}
	}
	
	private String buildUnknownActionFor(String message) {
		StringBuilder sb = new StringBuilder();
		sb.append("Lo siento, no sé qué hacer aún con la orden: \n");
		sb.append(italizedString(message));
		
		return sb.toString();
	}
	
	private String italizedString(String input) {
		return new String("_" + input + "_");
	}

	@Override
	public String getBotUsername() {
		LOGGER.debug("Bot username requested.");
		return this.username;
	}

	@Override
	public String getBotToken() {
		LOGGER.debug("Bot token requested.");
		return this.token;
	}

}

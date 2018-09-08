package es.parravidales.bot.telegram;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.google.common.io.Files;

import es.parravidales.bot.core.Orchestrator;

/**
 * @author victor.parra.vidales
 *
 */
@Component
public class RaspiBotImpl extends TelegramLongPollingBot implements RaspiBot {

	private static final Logger LOGGER = LoggerFactory.getLogger(RaspiBotImpl.class);
	
	private static final String MIME_TYPE = "application/x-bittorrent";
	private static final String TORRENT_EXT = ".torrent";

	@Value("${bot.telegram.username}")
	private String username;

	@Value("${bot.telegram.token}")
	private String token;
	
	@Value("${bot.telegram.userId}")
	private Integer userId;
	
	@Autowired
	private Orchestrator orchestrator;

	@Override
	public void onUpdateReceived(Update update) {
		LOGGER.debug("Update received: {}", update);
		if(Objects.nonNull(update) && Objects.nonNull(update.getMessage())) {
			Message message = update.getMessage();
			if(userId.equals(message.getFrom().getId())) {
				Long chatId = update.getMessage().getChatId();
				if(message.hasDocument()) {
					Document document = message.getDocument();
					if(Objects.isNull(document)) {
						LOGGER.warn("Recibido un documento nulo.");
						sendMessage(userId,
								sanitizeForMarkdown("Se ha recibido el documento como nulo. Vuelve a intentarlo."), 
								false);
					}
					LOGGER.debug("Document received: {}", document);
					if(MIME_TYPE.equals(document.getMimeType()) || document.getFileName().endsWith(TORRENT_EXT)) {
						LOGGER.debug("Recibido fichero torrent {}. Tamaño: {}", document.getFileName(), document.getFileSize());
						
						GetFile getFile = new GetFile();
						getFile.setFileId(document.getFileId());
						try {
							File file = downloadFile(execute(getFile));
							if(Objects.nonNull(file)) {
								this.orchestrator.saveTorrent(file, document.getFileName());
								sendMessage(userId, "Recibido fichero torrent _"+sanitizeForMarkdown(document.getFileName())+"_ (Tamaño: "+document.getFileSize()+" bytes). Depositado en la carpeta de torrents.", true);
							} else {
								LOGGER.error("No se ha podido descargar el fichero. Se ha obtenido un File: {}", file);
								sendMessage(userId, "No he podido descargar el fichero", false);
							}
						} catch (TelegramApiException e) {
							LOGGER.error("Error trying to download file {}", document.getFileName(), e);
							sendMessage(userId, "Error al obtener el fichero: " + e.getMessage(), false);
						} catch (IOException e) {
							LOGGER.error("Error trying to save file: {}", e.getMessage(), e);
							sendMessage(userId, "Error al guardar el fichero: " + e.getMessage(), false);
						}
						
					}
				} else if(message.hasText()) {
					sendMessage(chatId, 
							buildUnknownActionFor(update.getMessage().getText()),
							true);
				}
			} else {
				LOGGER.warn("Unknown userId received: {}", message.getFrom());
			}
		}
	}
	
	private String buildUnknownActionFor(String message) {
		StringBuilder sb = new StringBuilder();
		sb.append("Aún no hay nada para la orden: \n");
		sb.append(initalizedString(message));
		
		return sb.toString();
	}
	
	private String sanitizeForMarkdown(String input) {
		return input.replaceAll("_", " ");
	}
	
	private String initalizedString(String input) {
		return new String("_" + sanitizeForMarkdown(input) + "_");
	}
	
	private SendMessage responseMessage(long chatId, String text, boolean useMarkdown) {
		SendMessage responseMessage = new SendMessage() // Create a SendMessage object with mandatory fields
                .setChatId(chatId)
                .setText(text);
		
		if(useMarkdown) {
			responseMessage.setParseMode("Markdown");
		}
		
		return responseMessage;
	}
	
	private void sendMessage(long chatId, String text, boolean useMarkdown) {
		SendMessage message = responseMessage(chatId, text, useMarkdown);
		try {
            execute(message); // Call method to send the message
        } catch (TelegramApiException e) {
            LOGGER.error("Exception throwed while sending response message: {}", e.getMessage(), e);
        }
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

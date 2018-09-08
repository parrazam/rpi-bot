package es.parravidales.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;

/**
 * Main class for bot
 * To run this class, remember fill USER_ID and TOKEN_ID in application.yml
 * or define other profile and run with -Dspring.profiles.active=your_profile
 */
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		
		ApiContextInitializer.init();
		
		SpringApplication.run(Application.class, args);
	}
}

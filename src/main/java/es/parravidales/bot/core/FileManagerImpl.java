package es.parravidales.bot.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FileManagerImpl implements FileManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileManagerImpl.class);
			
	@Override
	public void saveFile(File file, String fullName) throws IOException {
		
		if(Objects.isNull(file)) {
			throw new IllegalArgumentException("File can't be null!");
		}
		
		if(Objects.isNull(fullName) || fullName.isEmpty()) {
			throw new IllegalArgumentException("Path can't be null!");
		}
		
		try {
			Files.write(Paths.get(fullName), Files.readAllBytes(file.toPath()));
		} catch (IOException e) {
			LOGGER.error("Error trying to save file {}. Error message: {}", file.getName(), e.getMessage(), e);
			throw e;
		}
	}

}

package es.parravidales.bot.core;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OrchestratorImpl implements Orchestrator {

	private static final Logger LOGGER = LoggerFactory.getLogger(OrchestratorImpl.class);
	
	@Value("${bot.torrent.path}")
	private String torrentPath;
	
	@Autowired
	private FileManager fileManager;
	
	@Override
	public void saveTorrent(File file, String fileName) throws IOException {
		
		fileManager.saveFile(file, torrentPath + fileName);
	}

}

package es.parravidales.bot.core;

import java.io.File;
import java.io.IOException;

public interface Orchestrator {

	void saveTorrent(File file, String fileName) throws IOException;
}

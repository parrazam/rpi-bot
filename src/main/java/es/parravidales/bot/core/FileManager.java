package es.parravidales.bot.core;

import java.io.File;
import java.io.IOException;

public interface FileManager {

	void saveFile(File file, String fullName) throws IOException;
}

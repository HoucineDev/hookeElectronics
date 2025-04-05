package application;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggerAncea {
	
	private static final String pathLog = Ressources.path+"\\Logs\\"+"logancea.log";
	
	static void setupLogger() throws IOException {
		
		Logger logger = Logger.getLogger(LoggerAncea.class.getName());
		
		Handler fichierLog = new FileHandler(pathLog);
		
		logger.setLevel(Level.ALL);
		logger.addHandler(fichierLog);
		
	}
	
}

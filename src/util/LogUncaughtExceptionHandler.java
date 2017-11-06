package util;

import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LogUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
	private FileHandler fileHandler;
	private int seed = 0;
	private String pathDir;

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		Logger logException = Logger.getLogger(pathDir+"Exception.log");
		try {
			Formatter aFormatter = new SimpleFormatter();
			fileHandler.setFormatter(aFormatter);
			logException.setLevel(Level.ALL);
			logException.setUseParentHandlers(false);
			logException.addHandler(fileHandler);
			logException.setUseParentHandlers(true);
			logException.info(t.getName() + ", seed fail: " + this.seed);
			logException.info(e.getMessage());
			Thread.sleep(500);
		} catch (SecurityException | InterruptedException e1) {
			e1.printStackTrace();
		}

	}

	/**
	 * @return the seed
	 */
	public int getSeed() {
		return seed;
	}

	/**
	 * @param seed the seed to set
	 */
	public void setSeed(int seed) {
		this.seed = seed;
	}

	/**
	 * @return the fileHandler
	 */
	public FileHandler getFileHandler() {
		return fileHandler;
	}

	/**
	 * @param fileHandler the fileHandler to set
	 */
	public void setFileHandler(FileHandler fileHandler) {
		this.fileHandler = fileHandler;
	}

	/**
	 * Returns the directory path
	 * @return a string
	 */
	public String getPathDir() {
		return pathDir;
	}

	/**
	 * Sets the directory path
	 * @param pathDir
	 */
	public void setPathDir(String pathDir) {
		this.pathDir = pathDir;
	}


}

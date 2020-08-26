package org.acme;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Demo {
    private boolean logToFile;
	private boolean logToConsole;
    private boolean logMessage;
    private boolean logWarning;
    private boolean logError;
    private boolean logToDatabase;
    private Map<?, ?> dbParams;
    private Logger logger;

    public Demo(boolean logToFileParam, boolean logToConsoleParam, boolean logToDatabaseParam,
                boolean logMessageParam, boolean logWarningParam, boolean logErrorParam, Map<?, ?> dbParamsMap) {
        logger = Logger.getLogger("MyLog");
        logError = logErrorParam;
        logMessage = logMessageParam;
        logWarning = logWarningParam;
        logToDatabase = logToDatabaseParam;
        logToFile = logToFileParam;
        logToConsole = logToConsoleParam;
        dbParams = dbParamsMap;
    }

    public void logMessage(String messageText, boolean message, boolean warning, boolean error) throws Exception {
        messageText = messageText.trim();
        if (messageText == null || messageText.length() == 0) {
            return;
        }
        if (!logToConsole && !logToFile && !logToDatabase) {
            throw new Exception("Invalid configuration");
        }
        if ((!logError && !logMessage && !logWarning) || (!message && !warning && !error)) {
            throw new Exception("Error or Warning or Message must be specified");
        }

        Connection connection = null;
        Properties connectionProps = new Properties();
        connectionProps.put("user", dbParams.get("userName"));
        connectionProps.put("password", dbParams.get("password"));

        try {
        	connection = DriverManager.getConnection("jdbc:" + dbParams.get("dbms") + "://" + dbParams.get("serverName")
            + ":" + dbParams.get("portNumber") + "/", connectionProps);
        	
            int t = 0;
            if (message && logMessage) {
                t = 1;
            }

            if (error && logError) {
                t = 2;
            }

            if (warning && logWarning) {
                t = 3;
            }

            Statement stmt = connection.createStatement();

            String l = null;
            File logFile = new File(dbParams.get("logFileFolder") + "/logFile.txt");
            if (!logFile.exists()) {
                logFile.createNewFile();
            }

            FileHandler fh = new FileHandler(dbParams.get("logFileFolder") + "/logFile.txt");
            ConsoleHandler ch = new ConsoleHandler();

            if (error && logError) {
                l = l + "error " + DateFormat.getDateInstance(DateFormat.LONG).format(new Date()) + messageText;
            }

            if (warning && logWarning) {
                l = l + "warning " +DateFormat.getDateInstance(DateFormat.LONG).format(new Date()) + messageText;
            }

            if (message && logMessage) {
                l = l + "message " +DateFormat.getDateInstance(DateFormat.LONG).format(new Date()) + messageText;
            }

            if(logToFile) {
                logger.addHandler(fh);
                logger.log(Level.INFO, messageText);
            }

            if(logToConsole) {
                logger.addHandler(ch);
                logger.log(Level.INFO, messageText);
            }

            if(logToDatabase) {
                stmt.executeUpdate("insert into Log_Values('" + message + "', " + t + ")");
            }
            stmt.close();
            connection.close();
        }catch(Exception e) {
        	e.printStackTrace();
        }
        
        


    }
    
    public boolean isLogToFile() {
		return logToFile;
	}

	public void setLogToFile(boolean logToFile) {
		this.logToFile = logToFile;
	}

	public boolean isLogToConsole() {
		return logToConsole;
	}

	public void setLogToConsole(boolean logToConsole) {
		this.logToConsole = logToConsole;
	}

	public boolean isLogMessage() {
		return logMessage;
	}

	public void setLogMessage(boolean logMessage) {
		this.logMessage = logMessage;
	}

	public boolean isLogWarning() {
		return logWarning;
	}

	public void setLogWarning(boolean logWarning) {
		this.logWarning = logWarning;
	}

	public boolean isLogError() {
		return logError;
	}

	public void setLogError(boolean logError) {
		this.logError = logError;
	}

	public boolean isLogToDatabase() {
		return logToDatabase;
	}

	public void setLogToDatabase(boolean logToDatabase) {
		this.logToDatabase = logToDatabase;
	}

	public Map getDbParams() {
		return dbParams;
	}

	public void setDbParams(Map dbParams) {
		this.dbParams = dbParams;
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}
}

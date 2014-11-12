/*
 * Copyright 2014
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pl.grzegorz2047.openguild2047;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import pl.grzegorz2047.openguild2047.api.Logger;

public class OGLogger implements Logger {
    
    private File logFile;
    private FileHandler handler;
    private Formatter formatter = new OGFormatter();
    private java.util.logging.Logger logger = java.util.logging.Logger.getLogger("OpenGuild");

    public OGLogger() {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        logFile = new File(String.format("plugins/OpenGuild2047/logger/%s.log", format.format(new Date())));
        
        if(!logFile.getParentFile().exists()) {
            logFile.getParentFile().mkdirs();
        }
        
        try {
            if(!logFile.exists()) {
                logFile.createNewFile();
            }
            
            handler = new FileHandler(logFile.getPath(), true);
            handler.setFormatter(formatter);
            
            logger.addHandler(handler);
        } catch(IOException e) {
            System.out.println("---- AN EXCEPTION HAS BEEN THROWN!");
            System.out.println("Message: " + e.getMessage());
            System.out.println("---- You can find entire report in log file!");
        }
    }

    @Override
    public void debug(String debug) {
        log(Level.INFO, debug);
    }

    @Override
    public void info(String info) {
        log(Level.INFO, info);
    }

    @Override
    public void log(Level level, String log) {
        logger.log(level, "[OpenGuild] {0}", log);
    }

    @Override
    public void severe(String severe) {
        log(Level.SEVERE, severe);
    }

    @Override
    public void warning(String warning) {
        log(Level.WARNING, warning);
    }
    
    public void exceptionThrown(Exception exception) {
        log(Level.SEVERE, "---- AN EXCEPTION HAS BEEN THROWN!");
        log(Level.SEVERE, exception.toString());
        log(Level.SEVERE, "---- You can find entire report in log file!");
    }
    
    public File getLoggingDirectory() {
        return logFile.getParentFile();
    }

    class OGFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            String message = formatMessage(record);
            return "[" + format.format(new Date(record.getMillis())) + "]" + message + System.getProperty("line.separator");
        }

    }
}

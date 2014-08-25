/**
 * The MIT License
 *
 * Copyright 2014 Grzegorz.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
            System.out.println("Cause: " + e.getCause().toString());
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
        log(Level.SEVERE, "Cause: " + exception.getCause().toString());
        log(Level.SEVERE, "Message: " + exception.getMessage());
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

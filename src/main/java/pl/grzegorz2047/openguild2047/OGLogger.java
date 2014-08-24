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
import java.util.Calendar;
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
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1; // It starts from 0.
        int year = calendar.get(Calendar.YEAR);
        
        logFile = new File(String.format("plugins/OpenGuild2047/logger/%s-%s-%s.log", year, month, day));
        
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
            e.printStackTrace();
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
    
    public File getLoggingDirectory() {
        return logFile.getParentFile();
    }

    class OGFormatter extends Formatter {
        private Calendar calendar;
        
        public OGFormatter() {
            this.calendar = Calendar.getInstance();
        }
        
        @Override
        public String format(LogRecord record) {
            calendar.setTimeInMillis(record.getMillis());
            
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH) + 1; // It starts from 0.
            int year = calendar.get(Calendar.YEAR);
            
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);
            
            String currentDate = String.format("%s.%s.%s %s:%s:%s", day, (month < 10 ? "0" + month : month), year, hour, (minute < 10 ? "0" + minute : minute), (second < 10 ? "0" + second : second));
            String message = formatMessage(record);
            return "[" + currentDate + "]" + message + System.getProperty("line.separator");
        }

    }
}

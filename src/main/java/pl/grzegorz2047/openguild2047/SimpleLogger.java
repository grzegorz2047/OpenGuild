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
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

import pl.grzegorz2047.openguild2047.api.Logger;

public class SimpleLogger implements Logger {

    private File dir = new File("plugins/OpenGuild2047/logger");
    private FileHandler file;
    private java.util.logging.Logger logger = java.util.logging.Logger.getLogger("OpenGuild");
    private SimpleFormatter formatter = new SimpleFormatter();

    public SimpleLogger() {
        try {
            if(!dir.exists()) {
                dir.mkdir();
            }
            file = new FileHandler("plugins/OpenGuild2047/logger/openguild.log");
            logger.addHandler(file);
            file.setFormatter(formatter);
        }
        catch(IOException ex) {
            ex.printStackTrace();
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

}

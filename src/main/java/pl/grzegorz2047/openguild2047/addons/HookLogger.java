/*
 * Copyright 2014 Aleksander.
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
package pl.grzegorz2047.openguild2047.addons;

import pl.grzegorz2047.openguild2047.interfaces.Logger;
import pl.grzegorz2047.openguild2047.BagOfEverything;
import java.io.File;
import java.util.logging.Level;

/**
 *
 * @author Aleksander
 */
public class HookLogger implements Logger {
    public static final String PREFIX = "[%s-hook] ";
    private final Hook hook;
    private static final Logger logger = BagOfEverything.getLogger();
    
    public HookLogger(Hook hook) {
        this.hook = hook;
    }
    
    @Override
    public void debug(String debug) {
        logger.debug(String.format(PREFIX, hook.getPlugin()) + debug);
    }

    @Override
    public void info(String info) {
        logger.info(String.format(PREFIX, hook.getPlugin()) + info);
    }

    @Override
    public void log(Level level, String log) {
        logger.log(level, String.format(PREFIX, hook.getPlugin()) + log);
    }

    @Override
    public void severe(String severe) {
        logger.severe(String.format(PREFIX, hook.getPlugin()) + severe);
    }

    @Override
    public void warning(String warning) {
        logger.warning(String.format(PREFIX, hook.getPlugin()) + warning);
    }

    @Override
    public void exceptionThrown(Exception exception) {
        logger.exceptionThrown(exception);
    }

    @Override
    public File getLoggingDirectory() {
        return logger.getLoggingDirectory();
    }
    
    public Hook getHook() {
        return hook;
    }
}

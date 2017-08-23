/*
 * The MIT License
 *
 * Copyright 2014 Adam.
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
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package pl.grzegorz2047.openguild2047.tasks;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * This class implements all Runnable methods, but adds
 * an option to stop task without knowing it's ID.
 */
public class EnhancedRunnable implements Runnable {
    /**
     * ID of this task.
     */
    private int taskID = -1;
    
    /**
     * This method should be overwritten by instance of this class.
     */
    public void run() { }
    
    /**
     * Changes task id to specified.
     * @param taskID new task id.
     */
    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    /**
     * @return task id
     */
    public int getTaskID() {
        return taskID;
    }
    
    /**
     * Starts new task.
     */
    public static void startTask(Plugin plugin, EnhancedRunnable runnable, long delay, long time) {
        int taskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, runnable, delay, time);
        runnable.setTaskID(taskID);
    }
    
    /**
     * Stops this task.
     */
    public void stopTask() {
        Bukkit.getServer().getScheduler().cancelTask(taskID);
    }
}

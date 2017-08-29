package pl.grzegorz2047.openguild.antilogout;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * File created by grzegorz2047 on 28.08.2017.
 */
public class AntiLogoutBarGeneratorTest {
    @Test
    public void generateActionBarAntilogoutWhenTimeIsZero() throws Exception {
        AntiLogoutBarGenerator antiLogoutBarGenerator = new AntiLogoutBarGenerator();
        int seconds = 0;
        String bar = antiLogoutBarGenerator.generateActionBarAntilogout(seconds);
        assertEquals("§6§lFairPlay Checker§4▉▉▉▉▉▉▉▉▉▉§e 0 §6sec.", bar);
    }

    @Test
    public void generateActionBarAntilogoutWhenTimeIsOne() throws Exception {
        AntiLogoutBarGenerator antiLogoutBarGenerator = new AntiLogoutBarGenerator();
        int seconds = 1;
        String bar = antiLogoutBarGenerator.generateActionBarAntilogout(seconds);
        assertEquals("§6§lFairPlay Checker§4▉▉▉▉▉▉▉▉▉▉§e 1 §6sec.", bar);
    }

    @Test
    public void generateActionBarAntilogoutWhenTimeIs20() throws Exception {
        AntiLogoutBarGenerator antiLogoutBarGenerator = new AntiLogoutBarGenerator();
        int seconds = 20;
        String bar = antiLogoutBarGenerator.generateActionBarAntilogout(seconds);
        assertEquals("§6§lFairPlay Checker§c▉▉▉▉▉§4▉▉▉▉▉§e 20 §6sec.", bar);
    }

    @Test
    public void generateActionBarAntilogoutWhenTimeIs10() throws Exception {
        AntiLogoutBarGenerator antiLogoutBarGenerator = new AntiLogoutBarGenerator();
        int seconds = 10;
        String bar = antiLogoutBarGenerator.generateActionBarAntilogout(seconds);
        assertEquals("§6§lFairPlay Checker§c▉▉§4▉▉▉▉▉▉▉▉§e 10 §6sec.", bar);
    }

}
package pl.grzegorz2047.openguild.cuboidmanagement;

import org.bukkit.Location;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class CuboidsTest {

    @Test
    public void previewCuboid() throws Exception {
        Cuboids cuboids = new Cuboids();
        Location sourceHome = new Location(null, 0, 0, 0);
        int cuboidSize = 15;
        String testTagGuildName = "TEST";
        Cuboid testCuboid = cuboids.previewCuboid(sourceHome, testTagGuildName, cuboidSize);
        assertEquals(sourceHome, testCuboid.getCenter());
        assertEquals(cuboidSize, testCuboid.getCuboidSize());
        
    }
}
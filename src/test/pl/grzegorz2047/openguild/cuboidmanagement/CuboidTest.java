package pl.grzegorz2047.openguild.cuboidmanagement;

import org.bukkit.Location;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class CuboidTest {

    @Test
    public void isColliding() throws Exception {
        UUID randomUUID = UUID.randomUUID();
        Cuboid testCuboid = new Cuboid(new Location(null, 0, 0, 0), "TEST", 15, randomUUID);
        Cuboid mehCuboid = new Cuboid(new Location(null, 1, 0, 0), "MEH", 15, randomUUID);
        Cuboid akhbarCuboid = new Cuboid(new Location(null, -14, 0, 0), "AKHBAR", 15, randomUUID);
        Cuboid alanCuboid = new Cuboid(new Location(null, 31, 0, 0), "ALAN", 15, randomUUID);

        boolean testCuboidColliding = testCuboid.isColliding(randomUUID, alanCuboid.getMin(), alanCuboid.getMax());
        boolean mehCuboidColliding = testCuboid.isColliding(randomUUID, mehCuboid.getMin(), mehCuboid.getMax());
        boolean akhbarCuboidColliding = testCuboid.isColliding(randomUUID, akhbarCuboid.getMin(), akhbarCuboid.getMax());
        assertEquals(false, testCuboidColliding);
        assertEquals(true, mehCuboidColliding);
        assertEquals(true, akhbarCuboidColliding);
    }

}

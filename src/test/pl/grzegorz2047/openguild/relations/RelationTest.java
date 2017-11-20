package pl.grzegorz2047.openguild.relations;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RelationTest {
    Relation relation = new Relation("TEST1", "TEST2", -1, Relation.Status.ALLY);

    @Test
    public void getBaseGuildTag() throws Exception {
        assertEquals("TEST1", relation.getBaseGuildTag());
    }

    @Test
    public void getAlliedGuildTag() throws Exception {
        assertEquals("TEST2", relation.getAlliedGuildTag());
    }

    @Test
    public void getState() throws Exception {
        assertEquals(Relation.Status.ALLY, relation.getState());
    }

    @Test
    public void setState() throws Exception {
    }
}

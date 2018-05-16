package byog.lab5;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestHexCal {
    @Test
    public void testRowWidth() {
        assertEquals(5, HexWorld.rowWidth(3, 4));
        assertEquals(8, HexWorld.rowWidth(4, 5));
    }

    @Test
    public void testXOffset() {
        assertEquals(-3, HexWorld.xOffset(4, 3));
    }
}

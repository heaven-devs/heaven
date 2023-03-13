package ga.heaven.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PeripheralServiceTest {
    public static final String GREETING_HAS_STRING =
            "frontend will be here soon";
    private final PeripheralService out = new PeripheralService();
    
    @Test
    void indexTest() {
        assertTrue(out.index().contains(GREETING_HAS_STRING));
    }
}
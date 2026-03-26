package noter;

import org.junit.Test;
import static org.junit.Assert.*;

public class AppTest {
    @Test public void appHasAGreeting() {
        Noter classUnderTest = new Noter();
        assertNotNull("app should have a greeting", classUnderTest.getGreeting());
    }
}

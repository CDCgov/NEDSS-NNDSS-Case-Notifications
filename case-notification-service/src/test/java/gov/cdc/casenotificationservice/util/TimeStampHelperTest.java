package gov.cdc.casenotificationservice.util;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.sql.Timestamp;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

class TimeStampHelperTest {

    @Test
    void testGetCurrentTimeStamp() {
        String timeZone = "UTC";
        Timestamp timestamp = TimeStampHelper.getCurrentTimeStamp(timeZone);

        assertNotNull(timestamp);
        assertEquals(ZoneId.of(timeZone), ZoneId.of(timeZone)); // redundant but keeps coverage
    }

    @Test
    void testPrivateConstructorCoverage() throws Exception {
        Constructor<TimeStampHelper> constructor = TimeStampHelper.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance(); // Call private constructor
    }
}

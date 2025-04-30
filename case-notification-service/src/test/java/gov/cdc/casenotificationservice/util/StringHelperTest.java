package gov.cdc.casenotificationservice.util;


import gov.cdc.casenotificationservice.model.Netss;
import gov.cdc.casenotificationservice.util.StringHelper;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class StringHelperTest {

    @Test
    void testStrRight_NullInput() {
        assertNull(StringHelper.strRight(null, 3));
    }

    @Test
    void testStrRight_ShortInput() {
        assertEquals("hi", StringHelper.strRight("hi", 5));
    }

    @Test
    void testStrRight_NormalCase() {
        assertEquals("rld", StringHelper.strRight("hello world", 3));
    }

    @Test
    void testStrLeft_NullInput() {
        assertNull(StringHelper.strLeft(null, 3));
    }

    @Test
    void testStrLeft_ShortInput() {
        assertEquals("yo", StringHelper.strLeft("yo", 5));
    }

    @Test
    void testStrLeft_NormalCase() {
        assertEquals("hel", StringHelper.strLeft("hello", 3));
    }

    @Test
    void testStrNumbers_NullInput() {
        assertEquals(0, StringHelper.strNumbers(null));
    }

    @Test
    void testStrNumbers_InvalidNumber() {
        assertEquals(0, StringHelper.strNumbers("abc"));
    }

    @Test
    void testStrNumbers_ValidNumber() {
        assertEquals(123, StringHelper.strNumbers("123"));
    }

    @Test
    void testBuildNetssSummary_AllEmptyFieldsWithLengths() throws Exception {
        Netss netss = new Netss();

        for (Field field : Netss.class.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getType().equals(String.class)) {
                field.set(netss, "");
            }
        }

        String result = StringHelper.buildNetssSummary(netss);

        // Expect only spaces (based on constants), trimmed to empty string
        assertEquals("", result);
    }

}

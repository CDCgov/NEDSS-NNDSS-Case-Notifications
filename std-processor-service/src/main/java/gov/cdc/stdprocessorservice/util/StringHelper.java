package gov.cdc.stdprocessorservice.util;

import gov.cdc.stdprocessorservice.model.Netss;

import java.lang.reflect.Field;

import static gov.cdc.stdprocessorservice.constant.StringConfig.*;

public class StringHelper {
    public static String strRight(String input, int numChars) {
        if (input == null || input.length() < numChars) {
            return input; // Return the input itself if invalid
        }
        return input.substring(input.length() - numChars);
    }

    public static String strLeft(String input, int numChars) {
        if (input == null || input.length() <= numChars) {
            return input; // Return the input itself if it's shorter or null
        }
        return input.substring(0, numChars);
    }

    public static Integer strNumbers(String input) {
        try {
            if (input == null) {
                return 0;
            }
            return Integer.valueOf(input);
        } catch (Exception e) {
            return 0;
        }
    }

    public static String buildNetssSummary(Netss netss) {
        StringBuilder sb = new StringBuilder();
        try {
            for (Field field : Netss.class.getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(netss);
                String fieldName = field.getName();

                // This strange setup is for RHAPSODY METSS specific
                if (value instanceof String str ) {
                    if (str.isEmpty() ) {
                        if (LENGTH_2.contains(fieldName)) {
                            sb.append("  "); // 2 spaces
                        } else if (LENGTH_3.contains(fieldName)) {
                            sb.append("   "); // 3 spaces
                        } else if (LENGTH_4.contains(fieldName)) {
                            sb.append("    "); // 4 spaces
                        } else if (LENGTH_5.contains(fieldName)) {
                            sb.append("     "); // 5 spaces
                        } else if (LENGTH_6.contains(fieldName)) {
                            sb.append("      "); // 6 spaces
                        } else if (LENGTH_8.contains(fieldName)) {
                            sb.append("        "); // 8 spaces
                        } else {
                            sb.append(" "); // default 1 space
                        }
                    }
                    else {
                        sb.append(str);

                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to access field in Netss", e);
        }

        return sb.toString().trim(); // Remove trailing space
    }
}

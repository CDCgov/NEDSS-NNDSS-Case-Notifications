package gov.cdc.stdprocessorservice.util;

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

}

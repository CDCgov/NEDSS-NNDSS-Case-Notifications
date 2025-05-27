package gov.cdc.xmlhl7parserservice.helper.mapper;

import org.springframework.stereotype.Component;

@Component
public class MapToRemoveSpecialCharacters {
    String mapToRemoveSpecialCharacters(String input) {
        String output = input;
        output = output.replace("\\", "\\E\\");
        output = output.replace("|", "\\F\\");
        output = output.replace("~", "\\R\\");
        output = output.replace("^", "\\S\\");
        output = output.replace("&", "\\T\\");
        return output;
    }
}

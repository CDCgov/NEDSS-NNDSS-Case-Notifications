package gov.cdc.xmlhl7parserservice.helper.mapper;

import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.v25.datatype.DTM;
import org.springframework.stereotype.Component;

@Component
public class MapToDTM18 {

    void mapToDTM18(String input, DTM output) throws DataTypeException {
        int stringSize = input.length();
        int year = Integer.parseInt(input.substring(0, 4));
        int month;
        int day;
        int hours;
        int minutes;
        float seconds;

        if (stringSize < 7) {
            month = 0;
        } else {
            month = Integer.parseInt(input.substring(4, 6));
        }

        if (stringSize < 10) {
            day = 0;
        } else {
            day = Integer.parseInt(input.substring(7, 9));
        }

        if (stringSize < 13) {
            hours = 0;
        } else {
            hours = Integer.parseInt(input.substring(10, 12));
        }

        if (stringSize < 16) {
            minutes = 0;
        } else {
            minutes = Integer.parseInt(input.substring(13, 15));
        }

        if (stringSize < 19) {
            seconds = 0;
        } else {
            seconds = Integer.parseInt(input.substring(16, 18));
        }

//        if(stringSize<23){
//            out.Time.Millis =000;
//            out.Time.seperator=".";
//        }else{
//            out.Time.seperator=".";
//            out.Time.Millis  = StrToInt(StrMid(in,20,3));
//        }

        output.setDateSecondPrecision(year, month, day, hours, minutes, seconds);
    }

}

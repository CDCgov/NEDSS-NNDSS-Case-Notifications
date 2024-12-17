package gov.cdc.stdprocessorservice.service;

import gov.cdc.stdprocessorservice.model.generated.jaxb.NBSNNDIntermediaryMessage;
import org.springframework.stereotype.Service;

@Service
public class MapperUtilService {
    public void mapToCodedAnswer() {
        // Lookup table
    }

    public String mapToData(NBSNNDIntermediaryMessage.MessageElement.DataElement input) {
        String output = "";
        if(input.getQuestionDataTypeNND().equalsIgnoreCase("CWE"))
        {
            output= input.getCweDataType().getCweCodedValue();
        }
        else if(input.getQuestionDataTypeNND().equalsIgnoreCase("CE"))
        {
            output= input.getCeDataType().getCeCodedValue();
        }
        else if(input.getQuestionDataTypeNND().equalsIgnoreCase("CX"))
        {
            output= input.getCxDataType().getCxData();
        }
        else if(input.getQuestionDataTypeNND().equalsIgnoreCase("IS"))
        {
            output= input.getIsDataType().getIsCodedValue();
        }
        else if(input.getQuestionDataTypeNND().equalsIgnoreCase("TX"))
        {
            output= input.getTxDataType().getTextData();
        }
        else if(input.getQuestionDataTypeNND().equalsIgnoreCase("ST"))
        {
            output= input.getStDataType().getStringData();
        }
        else if(input.getQuestionDataTypeNND().equalsIgnoreCase("SN"))
        {
            output= input.getSnDataType().getNum1().toString();
        }
        else if(input.getQuestionDataTypeNND().equalsIgnoreCase("DT"))
        {
            output= input.getDtDataType().getYear();
            if(output.isEmpty())
            {
                output= input.getDtDataType().getDate();
            }
        }
        else if(input.getQuestionDataTypeNND().equalsIgnoreCase("TS"))
        {
            output= input.getStDataType().getStringData();
        }
        
        return  output;
    }

    public String mapToDate(String week, String year, String output) {
        boolean result1 =true; //= RhapsodyTableLookup(output,"MMWR","WEEK_ENDING","NOT_MAPPED","MMWR_WEEK",week, "MMWR_YEAR", StrToUpper(year) );
        if (output.trim().isEmpty() || (week.equalsIgnoreCase("NULL") && year.equalsIgnoreCase("NULL"))) {
            output = "";
        }

        int checkerCode = output.indexOf("/");
        if (checkerCode > 0 && output.length() > 0) {
            String monthData= "";
            String dateData = "";
            String yearData= "";
            String displayName = "";

            String seperator= "^";
            String inputmodified= output;


            //int checkerCode = StrFind(inputmodified, "/");
            if (checkerCode > 0) {
                monthData = output.substring(0, checkerCode);
                if (monthData.length() == 1) {
                    monthData = "0" + monthData;
                }
                output = output.substring(checkerCode + 1);
            }
            checkerCode = output.indexOf("/");
            if (checkerCode > 0) {
                dateData = output.substring(0, checkerCode);
                if (dateData.length() == 1) {
                    dateData = "0" + dateData;
                }
                output = output.substring(checkerCode + 1);
            }

            yearData = output;
            if (yearData.length() == 1) {
                yearData = "0" + yearData;
            }

            // Construct the final output
            output = yearData + monthData + dateData;
        }

        return output;

    }

    public String mapToMultiCodedAnswer(String input, String questionCode, String toUniqueId, String output) {
        if(output.startsWith("CHECKER"))
        {
            boolean result  = true; //RhapsodyTableLookup(output,"NNDLookup","TO_UNIQUE_ID","NOT_MAPPED","FROM_UNIQUE_ID",questionCode, "CONCEPT_CD", StrToUpper(input) );
        }
        else{

            boolean result1  = true; //RhapsodyTableLookup(output,"NNDLookup","TO_CODE","NOT_MAPPED","FROM_UNIQUE_ID",questionCode, "TO_UNIQUE_ID", toUniqueId, "CONCEPT_CD", StrToUpper(input) );
            if (output.trim().isEmpty() || input.startsWith("NULL")) {
                output = "";
            }
        }

        return output;
    }

    public String mapToStringValue(NBSNNDIntermediaryMessage.MessageElement.DataElement input) {
        String output = "";
        if(input.getQuestionDataTypeNND().equalsIgnoreCase("CWE"))
        {
            output= input.getCweDataType().getCweCodedValue();
        }
        else if(input.getQuestionDataTypeNND().equalsIgnoreCase("CE"))
        {
            output= input.getCeDataType().getCeCodedValue();
        }
        else if(input.getQuestionDataTypeNND().equalsIgnoreCase("CX"))
        {
            output= input.getCxDataType().getCxData();
        }
        else if(input.getQuestionDataTypeNND().equalsIgnoreCase("IS"))
        {
            output= input.getIsDataType().getIsCodedValue();
        }
        else if(input.getQuestionDataTypeNND().equalsIgnoreCase("TX"))
        {
            output= input.getTxDataType().getTextData();
        }
        else if(input.getQuestionDataTypeNND().equalsIgnoreCase("ST"))
        {
            output= input.getStDataType().getStringData();
        }

        return output;
    }

    public String mapToTsType(String input, String output) {
        int checkerCode = input.indexOf("/");
        int checkerCodeDash = input.indexOf("-");

        if (checkerCode < 0 && checkerCodeDash < 0) {
            output = input;
        } else if (checkerCode > 0 && input.length() > 0) {
            String month = "";
            String date = "";
            String year = "";

            String inputModified = input;

            if (checkerCode > 0) {
                month = inputModified.substring(0, checkerCode);
                if (month.length() == 1) {
                    month = "0" + month;
                }
                inputModified = inputModified.substring(checkerCode + 1);
            }

            checkerCode = inputModified.indexOf("/");
            if (checkerCode > 0) {
                date = inputModified.substring(0, checkerCode);
                if (date.length() == 1) {
                    date = "0" + date;
                }
                inputModified = inputModified.substring(checkerCode + 1);
            }

            checkerCode = inputModified.indexOf(" ");
            if (checkerCode > 0) {
                year = inputModified.substring(0, checkerCode);
                inputModified = inputModified.substring(checkerCode + 1);
            } else {
                year = inputModified;
            }

            output = year + month + date;
        } else if (checkerCodeDash > 0 && input.length() > 0) {
            String month = "";
            String date = "";
            String year = "";

            String inputModified = input;

            if (checkerCodeDash > 0) {
                year = inputModified.substring(0, checkerCodeDash);
                inputModified = inputModified.substring(checkerCodeDash + 1);
            }

            checkerCodeDash = inputModified.indexOf("-");
            if (checkerCodeDash > 0) {
                month = inputModified.substring(0, checkerCodeDash);
                if (month.length() == 1) {
                    month = "0" + month;
                }
                inputModified = inputModified.substring(checkerCodeDash + 1);
            }

            if (inputModified.length() > 0) {
                date = inputModified.substring(0, 2);
            }

            output = year + month + date;
        }

        return output;
    }

}

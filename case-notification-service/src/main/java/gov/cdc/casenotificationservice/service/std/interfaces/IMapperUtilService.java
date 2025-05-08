package gov.cdc.casenotificationservice.service.std.interfaces;

import gov.cdc.casenotificationservice.model.generated.jaxb.MessageElement;

public interface IMapperUtilService {
    String mapToCodedAnswer(String intput, String questionCode);
    String mapToData(MessageElement.DataElement input);
    String mapToDate(String week, String year, String output);
    String mapToMultiCodedAnswer(String input, String questionCode, String toUniqueId, String output);
    String mapToStringValue(MessageElement.DataElement input);
    String mapToTsType(String input, String output);
}

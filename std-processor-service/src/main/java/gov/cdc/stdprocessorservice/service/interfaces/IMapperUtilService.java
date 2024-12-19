package gov.cdc.stdprocessorservice.service.interfaces;

import gov.cdc.stdprocessorservice.model.generated.jaxb.NBSNNDIntermediaryMessage;

public interface IMapperUtilService {
    String mapToCodedAnswer(String intput, String questionCode);
    String mapToData(NBSNNDIntermediaryMessage.MessageElement.DataElement input);
    String mapToDate(String week, String year, String output);
    String mapToMultiCodedAnswer(String input, String questionCode, String toUniqueId, String output);
    String mapToStringValue(NBSNNDIntermediaryMessage.MessageElement.DataElement input);
    String mapToTsType(String input, String output);
}

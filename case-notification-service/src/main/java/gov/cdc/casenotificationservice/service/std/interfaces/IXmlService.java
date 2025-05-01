package gov.cdc.casenotificationservice.service.std.interfaces;

import gov.cdc.casenotificationservice.model.MessageAfterStdChecker;
import jakarta.xml.bind.JAXBException;

public interface IXmlService {
    void mappingXmlStringToObject(MessageAfterStdChecker messageAfterStdChecker) ;
}

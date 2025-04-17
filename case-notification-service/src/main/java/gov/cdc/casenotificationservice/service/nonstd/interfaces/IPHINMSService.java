package gov.cdc.casenotificationservice.service.nonstd.interfaces;


import gov.cdc.casenotificationservice.model.PHINMSProperties;

public interface IPHINMSService {
    PHINMSProperties gettingPHIMNSProperties(
            String payload,
            PHINMSProperties PHINMSProperties) throws Exception;
}
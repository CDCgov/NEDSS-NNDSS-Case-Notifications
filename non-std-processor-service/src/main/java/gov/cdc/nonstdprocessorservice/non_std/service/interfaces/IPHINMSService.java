package gov.cdc.nonstdprocessorservice.non_std.service.interfaces;

import gov.cdc.nonstdprocessorservice.non_std.model.PHINMSProperties;

public interface IPHINMSService {
    public PHINMSProperties gettingPHIMNSProperties(
            String payload,
            PHINMSProperties PHINMSProperties) throws Exception;
}

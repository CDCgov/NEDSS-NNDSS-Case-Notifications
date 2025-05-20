package gov.cdc.casenotificationservice.service.common.interfaces;

import gov.cdc.casenotificationservice.exception.APIException;

public interface IApiService {
    String callToken();
    String callHl7Endpoint(String token, String recordId, boolean hl7ValidationEnabled) throws APIException;
}

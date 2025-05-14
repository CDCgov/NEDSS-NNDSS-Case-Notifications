package gov.cdc.casenotificationservice.service.common.interfaces;

import gov.cdc.casenotificationservice.exception.APIException;

public interface IApiService {
    String callHl7Endpoint(String token, String recordId) throws APIException;
}

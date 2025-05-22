package gov.cdc.casenotificationservice.service.common.interfaces;

import gov.cdc.casenotificationservice.model.ApiStatusResponseModel;

public interface IStatusService {
    ApiStatusResponseModel getProcessedStatus(Long cnTraportqOutId);
}

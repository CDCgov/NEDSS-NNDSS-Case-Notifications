package gov.cdc.nonstdprocessorservice.non_std.service;

import gov.cdc.nonstdprocessorservice.non_std.model.PHINMSProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BatchService {
    @Value("${service.batch-message-profile-id")
    private String BATCH_MESSAGE_PROFILE_ID = "MESSAGE_PROFILE_ID";

    public boolean isBatchConditionApplied(PHINMSProperties phinmsProperties) {
        if (phinmsProperties.getMessageControlID1().equals(BATCH_MESSAGE_PROFILE_ID)) {
            return true;
        }
        else {
            return false;
        }
    }

    public PHINMSProperties PopulateBatchFooterProperties(PHINMSProperties phinmsProperties) {
        var currentTime = phinmsProperties.getPCurrentTimestamp();
        var sendingApplication = phinmsProperties.getSENDING_APPLICATION();
        var sendingFacility = phinmsProperties.getSENDING_FACILITY();
        var SENDING_FACILITY_AND_NAME = sendingApplication+'|'+ sendingFacility;
        var header = "FHS|^~\\&|"
                + SENDING_FACILITY_AND_NAME
                + "|PHINCDS^2.16.840.1.114222.4.3.2.10^ISO|PHIN^2.16.840.1.114222^ISO|"
                + currentTime
                + "|||||\rBHS|^~\\&|"
                + SENDING_FACILITY_AND_NAME
                + "|PHINCDS^2.16.840.1.114222.4.3.2.10^ISO|PHIN^2.16.840.1.114222^ISO|"
                + currentTime
                + "|||||\r";

        var body = phinmsProperties.getPPHINMessageContent2();
        body = header + body;
        phinmsProperties.setPPHINMessageContent2(body);
        return phinmsProperties;
    }

    public void holdQueue() {
        // message need to be hold in the queue whenever HOLD QUEUE is activated
    }

    public void batch() {
        // Trailer - \rBTS|%c|\rFTS|1|
    }
}

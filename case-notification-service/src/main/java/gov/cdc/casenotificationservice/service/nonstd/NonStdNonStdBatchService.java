package gov.cdc.casenotificationservice.service.nonstd;

import gov.cdc.casenotificationservice.cache.NonStdQueue;
import gov.cdc.casenotificationservice.model.PHINMSProperties;
import gov.cdc.casenotificationservice.service.nonstd.interfaces.INonStdBatchService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NonStdNonStdBatchService implements INonStdBatchService {
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

    public PHINMSProperties ReleaseQueuePopulateBatchFooterProperties() {
        PHINMSProperties phinmsProperties = new PHINMSProperties();
        List<PHINMSProperties> queue = NonStdQueue.getInstance().getPhinmsPropertiesList();
        var batchHL7Msg = batch(queue);
        phinmsProperties = NonStdQueue.getInstance().getPhinmsPropertiesList().getFirst();
        NonStdQueue.getInstance().clearPHINMSProperties();

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

//        var body = phinmsProperties.getPPHINMessageContent2();
        var body = header + batchHL7Msg;
        phinmsProperties.setPPHINMessageContent2(body);
        return phinmsProperties;
    }

    /*
     * */
    public void holdQueue(PHINMSProperties phinmsProperties) {
        // message need to be hold in the queue whenever HOLD QUEUE is activated
        NonStdQueue.getInstance().addPHINMSProperties(phinmsProperties);

    }

    // this to batch the message after hold queue is release
    protected String batch(List<PHINMSProperties> queue) {
        StringBuilder stringBuilder = new StringBuilder();
        if (!queue.isEmpty()) {
            int counter = 0;
            // Trailer - \rBTS|%c|\rFTS|1|
            for(PHINMSProperties phinmsProperties : queue) {
                if (counter == 0) {
                    stringBuilder.append(phinmsProperties.getPPHINMessageContent2());
                }
                else
                {
                    stringBuilder.append("\r").append(phinmsProperties.getPPHINMessageContent2());
                }
                ++counter;
            }
            stringBuilder.append("\rBTS|");
            stringBuilder.append(counter);
            stringBuilder.append("|\rFTS|1|");
        }




        return stringBuilder.toString();
    }
}
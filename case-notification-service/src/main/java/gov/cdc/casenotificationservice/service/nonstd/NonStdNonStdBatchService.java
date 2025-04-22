package gov.cdc.casenotificationservice.service.nonstd;

import gov.cdc.casenotificationservice.cache.NonStdQueue;
import gov.cdc.casenotificationservice.model.PHINMSProperties;
import gov.cdc.casenotificationservice.repository.msg.model.CaseNotificationConfig;
import gov.cdc.casenotificationservice.service.nonstd.interfaces.INonStdBatchService;
import org.springframework.stereotype.Service;

import java.util.List;

import static gov.cdc.casenotificationservice.constant.NonStdConstantValue.*;

@Service
public class NonStdNonStdBatchService implements INonStdBatchService {

    public boolean isBatchConditionApplied(PHINMSProperties phinmsProperties,  CaseNotificationConfig stdConfig) {
        if (phinmsProperties.getMessageControlID1().equalsIgnoreCase(stdConfig.getBatchMesageProfileId())) {
            return true;
        }
        else {
            return false;
        }
    }

    public PHINMSProperties ReleaseQueuePopulateBatchFooterProperties() {
        PHINMSProperties phinmsProperties;
        List<PHINMSProperties> queue = NonStdQueue.getInstance().getPhinmsPropertiesList();
        var batchHL7Msg = batch(queue);
        phinmsProperties = NonStdQueue.getInstance().getPhinmsPropertiesList().getFirst();
        NonStdQueue.getInstance().clearPHINMSProperties();

        var currentTime = phinmsProperties.getPCurrentTimestamp();
        var SENDING_FACILITY_AND_NAME = phinmsProperties.getSENDING_APPLICATION() + HL7_PIPE + phinmsProperties.getSENDING_FACILITY();

        //        var header = "FHS|^~\\&|"
//                + SENDING_FACILITY_AND_NAME
//                + "|PHINCDS^2.16.840.1.114222.4.3.2.10^ISO|PHIN^2.16.840.1.114222^ISO|"
//                + currentTime
//                + "|||||\rBHS|^~\\&|"
//                + SENDING_FACILITY_AND_NAME
//                + "|PHINCDS^2.16.840.1.114222.4.3.2.10^ISO|PHIN^2.16.840.1.114222^ISO|"
//                + currentTime
//                + "|||||\r";

        String header = String.format(HL7_BATCH_HEADER_TEMPLATE,
                SENDING_FACILITY_AND_NAME,
                currentTime,
                SENDING_FACILITY_AND_NAME,
                currentTime);


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
                    stringBuilder.append(HL7_NEWLINE).append(phinmsProperties.getPPHINMessageContent2());
                }
                ++counter;
            }
            stringBuilder.append(HL7_BATCH_FOOTER_BTS);
            stringBuilder.append(counter);
            stringBuilder.append(HL7_BATCH_FOOTER_FTS);
        }




        return stringBuilder.toString();
    }
}
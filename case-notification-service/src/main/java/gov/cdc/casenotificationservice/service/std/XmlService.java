package gov.cdc.casenotificationservice.service.std;

import gov.cdc.casenotificationservice.exception.NonRetryableException;
import gov.cdc.casenotificationservice.exception.StdProcessorServiceException;
import gov.cdc.casenotificationservice.model.MessageAfterStdChecker;
import gov.cdc.casenotificationservice.model.Netss;
import gov.cdc.casenotificationservice.model.NetssPersistModel;
import gov.cdc.casenotificationservice.model.generated.jaxb.NBSNNDIntermediaryMessage;
import gov.cdc.casenotificationservice.repository.msg.NetssTransportQOutRepository;
import gov.cdc.casenotificationservice.repository.msg.model.NetssTransportQOut;
import gov.cdc.casenotificationservice.repository.odse.CNTraportqOutRepository;
import gov.cdc.casenotificationservice.service.std.interfaces.IStdMapperService;
import gov.cdc.casenotificationservice.service.std.interfaces.IXmlService;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.StringReader;

import static gov.cdc.casenotificationservice.util.StringHelper.buildNetssSummary;
import static gov.cdc.casenotificationservice.util.TimeStampHelper.getCurrentTimeStamp;

@Service
public class XmlService implements IXmlService {

    private final CNTraportqOutRepository cnTraportqOutRepository;
    private final IStdMapperService stdMapperService;
    private final NetssTransportQOutRepository netssTransportQOutRepository;
    @Value("${service.timezone}")
    private String tz = "UTC";

    public XmlService(CNTraportqOutRepository cnTraportqOutRepository,
                      IStdMapperService stdMapperService,
                      NetssTransportQOutRepository netssTransportQOutRepository) {
        this.cnTraportqOutRepository = cnTraportqOutRepository;
        this.stdMapperService = stdMapperService;
        this.netssTransportQOutRepository = netssTransportQOutRepository;
    }

    // pRecordStatus can be retrieved from DB
    public void mappingXmlStringToObject(MessageAfterStdChecker messageAfterStdChecker) throws StdProcessorServiceException, NonRetryableException {
        var cnTransportqOut = cnTraportqOutRepository.findTopByRecordUid(messageAfterStdChecker.getCnTransportqOutUid());
        String netssSummary;
        NetssPersistModel netssPersistModel = new NetssPersistModel();

        try {

            JAXBContext context = JAXBContext.newInstance(NBSNNDIntermediaryMessage.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            StringReader reader = new StringReader(cnTransportqOut.getMessagePayload());
            NBSNNDIntermediaryMessage msg = (NBSNNDIntermediaryMessage) unmarshaller.unmarshal(reader);
            Netss netss = stdMapperService.stdMapping(msg);
            netssSummary = buildNetssSummary(netss);
            netssPersistModel.setNetss(netssSummary);
            netssPersistModel.setVMessageYr(netss.getYear());
            netssPersistModel.setVCaseReptId(netss.getCaseReportId());
            netssPersistModel.setVMessageWeek(netss.getWeek());


            if (cnTransportqOut.getRecordStatusCd().equalsIgnoreCase("X")) {
                netssPersistModel.setRecordStatusCd("LOG_DEL");
            }
            else {
                netssPersistModel.setRecordStatusCd("ACTIVE");
            }
            cnTraportqOutRepository.updateStatus(cnTransportqOut.getCnTransportqOutUid(), "STD_COMPLETED");

            NetssTransportQOut netssTransportQOut = new NetssTransportQOut();
            netssTransportQOut.setRecordStatusCd("Individual Case");
            netssTransportQOut.setMmwrYear(Short.valueOf(netssPersistModel.getVMessageYr()));
            netssTransportQOut.setMmwrWeek(Short.valueOf(netssPersistModel.getVMessageWeek()));
            netssTransportQOut.setNetssCaseId(netssPersistModel.getVCaseReptId());
            netssTransportQOut.setPhcLocalId(cnTransportqOut.getPublicHealthCaseLocalId());
            netssTransportQOut.setNotificationLocalId(cnTransportqOut.getNotificationLocalId());
            netssTransportQOut.setPayload(netssSummary);
            netssTransportQOut.setRecordStatusCd(netssPersistModel.getRecordStatusCd());
            netssTransportQOut.setAddTime(getCurrentTimeStamp(tz));
            netssTransportQOut.setRecordTypeCd("Individual Case");
            try {
                netssTransportQOutRepository.save(netssTransportQOut);
            } catch (Exception e) {
                e.printStackTrace();
                throw new NonRetryableException(e.getMessage(), e);
            }
        } catch (Exception e) {
            e.printStackTrace();
            cnTraportqOutRepository.updateStatus(cnTransportqOut.getCnTransportqOutUid(), "STD_ERR");
            if (e instanceof NonRetryableException)
            {
                throw new NonRetryableException(e.getMessage(), e);
            }
            else
            {
                throw new StdProcessorServiceException("Error While Processing NETSS", e);
            }
        }


    }
}

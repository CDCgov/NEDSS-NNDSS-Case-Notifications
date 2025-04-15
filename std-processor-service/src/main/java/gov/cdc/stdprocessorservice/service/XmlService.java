package gov.cdc.stdprocessorservice.service;

import com.google.gson.Gson;
import gov.cdc.stdprocessorservice.model.Netss;
import gov.cdc.stdprocessorservice.model.NetssPersistModel;
import gov.cdc.stdprocessorservice.model.generated.jaxb.NBSNNDIntermediaryMessage;
import gov.cdc.stdprocessorservice.repository.msg.NetssTransportQOutRepository;
import gov.cdc.stdprocessorservice.repository.msg.model.NetssTransportQOut;
import gov.cdc.stdprocessorservice.repository.odse.CNTraportqOutRepository;
import gov.cdc.stdprocessorservice.repository.odse.model.CNTransportqOut;
import gov.cdc.stdprocessorservice.service.interfaces.IStdMapperService;
import gov.cdc.stdprocessorservice.service.interfaces.IXmlService;
import jakarta.annotation.PostConstruct;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.gson.GsonBuilderCustomizer;
import org.springframework.stereotype.Service;

import java.io.StringReader;

import static gov.cdc.stdprocessorservice.util.StringHelper.buildNetssSummary;
import static gov.cdc.stdprocessorservice.util.TimeStampHelper.getCurrentTimeStamp;

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

    @PostConstruct
    public void init() {
        var test = cnTraportqOutRepository.findTopByRecordStatusCdAndUid("UNPROCESSED", 23187L);
        Gson gson = new Gson();

        mappingXmlStringToObject(gson.toJson(test));
    }

    // pRecordStatus can be retrieved from DB
    public void mappingXmlStringToObject(String cnTransportQ) {
        Gson gson = new Gson();
        CNTransportqOut cnTransportqOut = gson.fromJson(cnTransportQ, CNTransportqOut.class);
        NetssPersistModel netssPersistModel = new NetssPersistModel();
        try {
            JAXBContext context = JAXBContext.newInstance(NBSNNDIntermediaryMessage.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            StringReader reader = new StringReader(cnTransportqOut.getMessagePayload());
            NBSNNDIntermediaryMessage msg = (NBSNNDIntermediaryMessage) unmarshaller.unmarshal(reader);
            Netss netss = stdMapperService.stdMapping(msg);
            var netssSummary = buildNetssSummary(netss);netssPersistModel.setNetss(netssSummary);
            netssPersistModel.setVMessageYr(netss.getYear());
            netssPersistModel.setVCaseReptId(netss.getCaseReportId());
            netssPersistModel.setVMessageWeek(netss.getWeek());


            if (cnTransportqOut.getRecordStatusCd().equalsIgnoreCase("X")) {
                netssPersistModel.setRecordStatusCd("LOG_DEL");
            }
            else {
                netssPersistModel.setRecordStatusCd("ACTIVE");
            }
            NetssTransportQOut netssTransportQOut = new NetssTransportQOut(netssPersistModel, cnTransportqOut);
            netssTransportQOut.setAddTime(getCurrentTimeStamp(tz));

            netssTransportQOutRepository.save(netssTransportQOut);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
}

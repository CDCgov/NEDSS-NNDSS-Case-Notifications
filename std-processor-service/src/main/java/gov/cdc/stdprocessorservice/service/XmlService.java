package gov.cdc.stdprocessorservice.service;

import gov.cdc.stdprocessorservice.model.Netss;
import gov.cdc.stdprocessorservice.model.NetssPersistModel;
import gov.cdc.stdprocessorservice.model.generated.jaxb.NBSNNDIntermediaryMessage;
import gov.cdc.stdprocessorservice.repository.odse.CNTraportqOutRepository;
import gov.cdc.stdprocessorservice.service.interfaces.IStdMapperService;
import gov.cdc.stdprocessorservice.service.interfaces.IXmlService;
import jakarta.annotation.PostConstruct;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.stereotype.Service;

import java.io.StringReader;

@Service
public class XmlService implements IXmlService {

    private final CNTraportqOutRepository cnTraportqOutRepository;
    private final IStdMapperService stdMapperService;

    public XmlService(CNTraportqOutRepository cnTraportqOutRepository, IStdMapperService stdMapperService) {
        this.cnTraportqOutRepository = cnTraportqOutRepository;
        this.stdMapperService = stdMapperService;
    }

    @PostConstruct
    public void init() {
        var test = cnTraportqOutRepository.findTopByRecordStatusCdAndUid("UNPROCESSED", 23187L);
        mappingXmlStringToObject(test.getMessagePayload());
    }

    // pRecordStatus can be retrieved from DB
    public void mappingXmlStringToObject(String xml) {
        NetssPersistModel netssPersistModel = new NetssPersistModel();
        try {
            JAXBContext context = JAXBContext.newInstance(NBSNNDIntermediaryMessage.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            StringReader reader = new StringReader(xml);
            NBSNNDIntermediaryMessage msg = (NBSNNDIntermediaryMessage) unmarshaller.unmarshal(reader);
            Netss netss = stdMapperService.stdMapping(msg);
            netssPersistModel.setNetss(netss);
            netssPersistModel.setVMessageYr(netss.getYear());
            netssPersistModel.setVCaseReptId(netss.getCaseReportId());
            netssPersistModel.setVMessageWeek(netss.getWeek());
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
}

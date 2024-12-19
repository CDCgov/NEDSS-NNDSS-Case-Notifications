package gov.cdc.stdprocessorservice.service;

import gov.cdc.stdprocessorservice.model.generated.jaxb.NBSNNDIntermediaryMessage;
import gov.cdc.stdprocessorservice.repository.odse.CNTraportqOutRepository;
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

    public XmlService(CNTraportqOutRepository cnTraportqOutRepository) {
        this.cnTraportqOutRepository = cnTraportqOutRepository;
    }

//    @PostConstruct
    public void init() {
        var test = cnTraportqOutRepository.findTopByRecordStatusCd("UNPROCESSED");
        mappingXmlStringToObject(test.getMessagePayload());
    }
    public void mappingXmlStringToObject(String xml) {
        try {
            JAXBContext context = JAXBContext.newInstance(NBSNNDIntermediaryMessage.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            StringReader reader = new StringReader(xml);
            NBSNNDIntermediaryMessage msg = (NBSNNDIntermediaryMessage) unmarshaller.unmarshal(reader);
            String test = "tesT";
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
}

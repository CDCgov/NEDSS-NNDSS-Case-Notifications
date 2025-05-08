package gov.cdc.casenotificationservice.service.std;


import gov.cdc.casenotificationservice.model.Netss;
import gov.cdc.casenotificationservice.model.generated.jaxb.NBSNNDIntermediaryMessage;
import gov.cdc.casenotificationservice.service.std.interfaces.IStdMapperService;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.InputStream;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class StdMapperServiceTest {

    private MapperUtilService mapperUtilService;
    private IStdMapperService stdMapperService;

    @BeforeEach
    void setUp() {
        mapperUtilService = Mockito.mock(MapperUtilService.class);
        stdMapperService = new StdMapperService(mapperUtilService);

        // Mock common behavior for mapToData and mapToCodedAnswer
        when(mapperUtilService.mapToData(any())).thenReturn("999999999");
        when(mapperUtilService.mapToCodedAnswer(anyString(), anyString())).thenReturn("1");
        when(mapperUtilService.mapToCodedAnswer(anyString(), any())).thenReturn("1");
        when(mapperUtilService.mapToTsType(anyString(), anyString())).thenReturn("20240101");
        when(mapperUtilService.mapToMultiCodedAnswer(anyString(), anyString(), anyString(), anyString())).thenReturn("Y");
        when(mapperUtilService.mapToDate(anyString(), anyString(), anyString())).thenReturn("20240101");
    }

    @Test
    void testStdMapping_FromSampleXml_ShouldMapCorrectly() throws Exception {
        // Arrange: Read test XML from resources
        InputStream is = getClass().getClassLoader().getResourceAsStream("std_test.txt");
        assertNotNull(is, "std_test.txt not found in resources");

        String xml = new String(is.readAllBytes());
        JAXBContext context = JAXBContext.newInstance(NBSNNDIntermediaryMessage.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        NBSNNDIntermediaryMessage message = (NBSNNDIntermediaryMessage) unmarshaller.unmarshal(new StringReader(xml));

        // Act
        Netss netss = stdMapperService.stdMapping(message);

        // Assert
        assertNotNull(netss);
        assertEquals("04", netss.getNetssVersion());
        assertEquals("9", netss.getSex());
        assertEquals("1", netss.getCaseStatus());
        assertEquals("99999", netss.getFuture());
        assertEquals("240101", netss.getEventDate());
        assertEquals("20240101", netss.getDateOfBirth());
        assertEquals("999", netss.getTotalNbrSexPartner12Month());

        // Validate race mapping (multi coded answer)
        assertEquals("U", netss.getAsian());
        assertEquals("U", netss.getBlackAfricanAmerican());
        assertEquals("U", netss.getWhite());
    }
}

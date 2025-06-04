package gov.cdc.casenotificationservice.service.std;


import gov.cdc.casenotificationservice.model.MessageAfterStdChecker;
import gov.cdc.casenotificationservice.model.Netss;
import gov.cdc.casenotificationservice.repository.msg.NetssTransportQOutRepository;
import gov.cdc.casenotificationservice.repository.msg.model.NetssTransportQOut;
import gov.cdc.casenotificationservice.repository.odse.CNTraportqOutRepository;
import gov.cdc.casenotificationservice.repository.odse.model.CNTransportqOut;
import gov.cdc.casenotificationservice.service.std.interfaces.IStdMapperService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class XmlServiceTest {

    private CNTraportqOutRepository cnTraportqOutRepository;
    private IStdMapperService stdMapperService;
    private NetssTransportQOutRepository netssTransportQOutRepository;
    private XmlService xmlService;

    @BeforeEach
    void setUp() {
        cnTraportqOutRepository = mock(CNTraportqOutRepository.class);
        stdMapperService = mock(IStdMapperService.class);
        netssTransportQOutRepository = mock(NetssTransportQOutRepository.class);
        xmlService = new XmlService(cnTraportqOutRepository, stdMapperService, netssTransportQOutRepository);
    }

    @Test
    void testMappingXmlStringToObject_whenActiveRecord() throws Exception {
        // Arrange
        MessageAfterStdChecker checker = new MessageAfterStdChecker();
        checker.setCnTransportqOutUid(123L);

        CNTransportqOut cnTransportqOut = new CNTransportqOut();
        cnTransportqOut.setMessagePayload(readFileFromResources("std_test.txt"));
        cnTransportqOut.setRecordStatusCd("A");

        when(cnTraportqOutRepository.findTopByRecordUid(123L)).thenReturn(cnTransportqOut);

        Netss mappedNetss = new Netss();
        mappedNetss.setCaseReportId("CASE123");
        mappedNetss.setYear("2025");
        mappedNetss.setWeek("16");

        when(stdMapperService.stdMapping(any())).thenReturn(mappedNetss);

        // Act
        xmlService.mappingXmlStringToObject(checker);

        // Assert
        ArgumentCaptor<NetssTransportQOut> captor = ArgumentCaptor.forClass(NetssTransportQOut.class);
        verify(netssTransportQOutRepository, times(1)).save(captor.capture());

        NetssTransportQOut savedObject = captor.getValue();
        assertEquals("CASE123", savedObject.getNetssCaseId());
        assertEquals("2025", savedObject.getMmwrYear().toString());
        assertEquals("16", savedObject.getMmwrWeek().toString());
        assertEquals("ACTIVE", savedObject.getRecordStatusCd());
    }

    @Test
    void testMappingXmlStringToObject_whenDeletedRecord() throws Exception {
        // Arrange
        MessageAfterStdChecker checker = new MessageAfterStdChecker();
        checker.setCnTransportqOutUid(456L);

        CNTransportqOut cnTransportqOut = new CNTransportqOut();
        cnTransportqOut.setMessagePayload(readFileFromResources("std_test.txt"));
        cnTransportqOut.setRecordStatusCd("X");

        when(cnTraportqOutRepository.findTopByRecordUid(456L)).thenReturn(cnTransportqOut);

        Netss mappedNetss = new Netss();
        mappedNetss.setCaseReportId("CASE456");
        mappedNetss.setYear("2024");
        mappedNetss.setWeek("10");

        when(stdMapperService.stdMapping(any())).thenReturn(mappedNetss);

        // Act
        xmlService.mappingXmlStringToObject(checker);

        // Assert
        ArgumentCaptor<NetssTransportQOut> captor = ArgumentCaptor.forClass(NetssTransportQOut.class);
        verify(netssTransportQOutRepository, times(1)).save(captor.capture());

        NetssTransportQOut savedObject = captor.getValue();
        assertEquals("CASE456", savedObject.getNetssCaseId());
        assertEquals("2024", savedObject.getMmwrYear().toString());
        assertEquals("10", savedObject.getMmwrWeek().toString());
        assertEquals("LOG_DEL", savedObject.getRecordStatusCd());
    }

    private String readFileFromResources(String filename) throws IOException, URISyntaxException {
        Path path = Path.of(Objects.requireNonNull(
                getClass().getClassLoader().getResource(filename)).toURI()
        );
        return Files.readString(path);
    }
}
package gov.cdc.casenotificationservice.service.common;

import gov.cdc.casenotificationservice.exception.DltServiceException;
import gov.cdc.casenotificationservice.kafka.producer.CaseNotificationProducer;
import gov.cdc.casenotificationservice.model.ApiDltResponseModel;
import gov.cdc.casenotificationservice.model.MessageAfterStdChecker;
import gov.cdc.casenotificationservice.repository.msg.CaseNotificationDltRepository;
import gov.cdc.casenotificationservice.repository.msg.model.CaseNotificationDlt;
import gov.cdc.casenotificationservice.repository.odse.CNTraportqOutRepository;
import gov.cdc.casenotificationservice.repository.odse.model.CNTransportqOut;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class DltServiceTest {

    @Mock
    private CaseNotificationDltRepository dltRepository;

    @Mock
    private CNTraportqOutRepository cnTraportqOutRepository;

    @Mock
    private CaseNotificationProducer producer;

    @InjectMocks
    private DltService dltService;

    private final String jsonPayload = "{\"cnTransportqOutUid\":12345}";
    private final String uuid = UUID.randomUUID().toString();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreatingDlt() {
        CNTransportqOut mockTransport = new CNTransportqOut();
        mockTransport.setMessagePayload("mock-payload");

        when(cnTraportqOutRepository.findTopByRecordUid(12345L)).thenReturn(mockTransport);

        dltService.creatingDlt(jsonPayload, "non-std-topic", "stacktrace", "root");

        verify(dltRepository, times(1)).save(any(CaseNotificationDlt.class));
    }

    @Test
    public void testGetDltsBetweenWithPagination() {
        Timestamp from = Timestamp.valueOf("2025-05-01 00:00:00");
        Timestamp to = Timestamp.valueOf("2025-05-02 00:00:00");
        Page<CaseNotificationDlt> page = new PageImpl<>(List.of(new CaseNotificationDlt()));

        when(dltRepository.findByCreatedOnBetween(eq(from), eq(to), any(Pageable.class))).thenReturn(page);

        Page<CaseNotificationDlt> result = dltService.getDltsBetweenWithPagination(from, to, 0, 10);

        verify(dltRepository).findByCreatedOnBetween(eq(from), eq(to), any(Pageable.class));
    }

    @Test
    public void testGetDltByUid_success() throws DltServiceException {
        CaseNotificationDlt dlt = new CaseNotificationDlt();
        dlt.setOriginalPayload(jsonPayload);

        when(dltRepository.findById(UUID.fromString(uuid))).thenReturn(Optional.of(dlt));

        dltService.getDltByUid(uuid);

        verify(dltRepository).findById(UUID.fromString(uuid));
    }

    @Test
    public void testGetDltByUid_notFound() {
        when(dltRepository.findById(UUID.fromString(uuid))).thenReturn(Optional.empty());

        try {
            dltService.getDltByUid(uuid);
            throw new RuntimeException("Expected DltServiceException to be thrown");
        } catch (DltServiceException e) {
            // expected
        }
    }

    @Test
    public void testReprocessingCaseNotification_success() throws DltServiceException {
        CaseNotificationDlt dlt = new CaseNotificationDlt();
        dlt.setOriginalPayload(jsonPayload);
        dlt.setSource("non-std-topic");
        dlt.setCnTranportqOutUid(123L);

        when(dltRepository.findById(UUID.fromString(uuid))).thenReturn(Optional.of(dlt));
        CNTransportqOut mockTransport = new CNTransportqOut();
        mockTransport.setMessagePayload("mock-payload");
        mockTransport.setCnTransportqOutUid(123L);
        when(cnTraportqOutRepository.findTopByRecordUid(123L)).thenReturn(mockTransport);
        dltService.reprocessingCaseNotification(jsonPayload, uuid);

        verify(dltRepository).save(any(CaseNotificationDlt.class));
    }

    @Test
    public void testReprocessingCaseNotification_notFound() {
        when(dltRepository.findById(UUID.fromString(uuid))).thenReturn(Optional.empty());

        try {
            dltService.reprocessingCaseNotification(jsonPayload, uuid);
            throw new RuntimeException("Expected DltServiceException to be thrown");
        } catch (DltServiceException e) {
            // expected
        }
    }
}
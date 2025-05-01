package gov.cdc.dataextractionservice.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.mockito.Mockito.*;

class CnTransportQOutUpdateServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private CnTransportQOutUpdateService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdateRecordStatus_Success() {
        long uid = 100L;
        String status = "COMPLETED";

        when(jdbcTemplate.update(anyString(), eq(status), eq(uid))).thenReturn(1);

        service.updateRecordStatus(uid, status);

        verify(jdbcTemplate).update(anyString(), eq(status), eq(uid));
    }

    @Test
    void testUpdateRecordStatus_NoRowsAffected() {
        long uid = 101L;
        String status = "FAILED";

        when(jdbcTemplate.update(anyString(), eq(status), eq(uid))).thenReturn(0);

        service.updateRecordStatus(uid, status);

        verify(jdbcTemplate).update(anyString(), eq(status), eq(uid));
    }
}

package gov.cdc.casenotificationservice.service.cntransportqout;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UpdateService {

  private final JdbcTemplate jdbcTemplate;

  public UpdateService(@Qualifier("odseJdbcTemplate") JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public void updateRecordStatus(long cnTransportqOutUid, String newStatus) {
    String sql =
        "UPDATE CN_transportq_out "
            + "SET record_status_cd = ?, record_status_time = GETDATE() "
            + "WHERE cn_transportq_out_uid = ?";

    int rows = jdbcTemplate.update(sql, newStatus, cnTransportqOutUid);

    if (rows > 0) {
      log.info(
          "Successfully updated record_status_cd to '{}' for cn_transportq_out_uid={}",
          newStatus,
          cnTransportqOutUid);
    } else {
      log.warn("No record updated for cn_transportq_out_uid={}", cnTransportqOutUid);
    }
  }
}

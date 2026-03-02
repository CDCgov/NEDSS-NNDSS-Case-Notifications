package gov.cdc.casenotificationservice.repository.msg;

import gov.cdc.casenotificationservice.repository.msg.model.ServiceActionPair;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ServiceActionPairRepository {
  private final JdbcTemplate msgJdbcTemplate;

  public ServiceActionPairRepository(@Qualifier("msgJdbcTemplate") JdbcTemplate msgJdbcTemplate) {
    this.msgJdbcTemplate = msgJdbcTemplate;
  }

  //    @Query(
  //            value = "SELECT * FROM SERVICE_ACTION_PAIR WHERE SERVICE = 'TOTAL'",
  //            nativeQuery = true
  //    )
  //    ServiceActionPair findTotal();

  public List<ServiceActionPair> findTotal() {
    String sql = "SELECT * FROM SERVICE_ACTION_PAIR WHERE SERVICE = 'TOTAL'";
    List<ServiceActionPair> tableList =
        msgJdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ServiceActionPair.class));
    return tableList;
  }
}

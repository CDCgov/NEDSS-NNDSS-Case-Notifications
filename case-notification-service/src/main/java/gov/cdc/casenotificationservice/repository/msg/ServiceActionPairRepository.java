package gov.cdc.casenotificationservice.repository.msg;

import gov.cdc.casenotificationservice.repository.msg.model.ServiceActionPair;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceActionPairRepository
{
    private final JdbcTemplate msgJdbcTemplate;

    public ServiceActionPairRepository( @Qualifier("msgJdbcTemplate") JdbcTemplate msgJdbcTemplate) {
        this.msgJdbcTemplate = msgJdbcTemplate;
    }

//    @Query(
//            value = "SELECT * FROM SERVICE_ACTION_PAIR WHERE SERVICE = 'TOTAL'",
//            nativeQuery = true
//    )
//    ServiceActionPair findTotal();

    public List<ServiceActionPair> findTotal() {
        String sql = "SELECT * FROM SERVICE_ACTION_PAIR WHERE SERVICE = 'TOTAL'";
        List<ServiceActionPair> tableList = msgJdbcTemplate.query(
                sql,
                new BeanPropertyRowMapper<>(ServiceActionPair.class));
        return tableList;
    }
}
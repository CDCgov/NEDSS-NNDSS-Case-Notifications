package gov.cdc.casenotificationservice.repository.msg;

import gov.cdc.casenotificationservice.repository.msg.model.ServiceActionPair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceActionPairRepository extends JpaRepository<ServiceActionPair, String> {
    @Query(
            value = "SELECT * FROM SERVICE_ACTION_PAIR WHERE SERVICE = 'TOTAL'",
            nativeQuery = true
    )
    ServiceActionPair findTotal();
}
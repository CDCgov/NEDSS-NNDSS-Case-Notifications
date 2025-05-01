package gov.cdc.casenotificationservice.repository.msg;


import gov.cdc.casenotificationservice.repository.msg.model.CaseNotificationDlt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.UUID;

@Repository
public interface CaseNotificationDltRepository extends JpaRepository<CaseNotificationDlt, UUID> {
    Page<CaseNotificationDlt> findByCreatedOnBetween(Timestamp start, Timestamp end, Pageable pageable);
}

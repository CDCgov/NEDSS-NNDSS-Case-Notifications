package gov.cdc.casenotificationservice.repository.msg;

import gov.cdc.casenotificationservice.repository.msg.model.CaseNotificationConfig;
import gov.cdc.casenotificationservice.repository.msg.model.LookupMmwr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;

@Repository
public interface CaseNotificationConfigRepository extends JpaRepository<CaseNotificationConfig, Integer> {
    @Query(
            value = "SELECT TOP 1 * FROM NBS_Case_Notification_Config WHERE config_applied = 1 AND config_name = 'NON_STD_CASE_NOTIFICATION';",
            nativeQuery = true
    )
    CaseNotificationConfig findNonStdConfig();
}

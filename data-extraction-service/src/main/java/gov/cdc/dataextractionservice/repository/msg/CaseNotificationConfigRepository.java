package gov.cdc.dataextractionservice.repository.msg;

import gov.cdc.dataextractionservice.repository.msg.model.CaseNotificationConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CaseNotificationConfigRepository extends JpaRepository<CaseNotificationConfig, Integer> {
    @Query(
            value = "SELECT TOP 1 * FROM NBS_Case_Notification_Config WHERE config_name = 'NON_STD_CASE_NOTIFICATION';",
            nativeQuery = true
    )
    CaseNotificationConfig findNonStdConfig();
}

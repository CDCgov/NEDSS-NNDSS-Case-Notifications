package gov.cdc.casenotificationservice.repository.msg;

import gov.cdc.casenotificationservice.repository.msg.model.CaseNotificationConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CaseNotificationConfigRepository extends JpaRepository<CaseNotificationConfig, Integer> {
    @Query(
            value = "SELECT TOP 1 * FROM NBS_Case_Notification_Config WHERE config_applied = 1 AND config_name = 'NON_STD_CASE_NOTIFICATION';",
            nativeQuery = true
    )
    CaseNotificationConfig findNonStdConfig();

    @Query(
            value = "SELECT TOP 1 * FROM NBS_Case_Notification_Config WHERE config_applied = 1 AND config_name = :configName;",
            nativeQuery = true
    )
    CaseNotificationConfig findConfigByName(@Param("configName") String configName);



    @Query(
            value = "SELECT CASE WHEN EXISTS (" +
                    "SELECT 1 FROM NBS_Case_Notification_Config " +
                    "WHERE config_applied = 1 AND config_name = 'NON_STD_CASE_NOTIFICATION')" +
                    " THEN CAST(1 AS BIT) ELSE CAST(0 AS BIT) END",
            nativeQuery = true
    )
    boolean isConfigApplied();

    @Query(
            value =
                    "SELECT hl7_validation_enabled FROM NBS_Case_Notification_Config " +
                    "WHERE config_applied = 1 AND config_name = 'NON_STD_CASE_NOTIFICATION'",
            nativeQuery = true
    )
    boolean isHl7ValidationApplied();


    @Modifying
    @Query(
            value = "UPDATE NBS_Case_Notification_Config SET config_applied = :applied " +
                    "WHERE id = :id",
            nativeQuery = true
    )
    int updateConfigAppliedById(@Param("id") int id, @Param("applied") int applied);

    @Modifying
    @Query(
            value = "UPDATE NBS_Case_Notification_Config SET config_applied = :applied " +
                    "WHERE id = (SELECT TOP 1 id FROM NBS_Case_Notification_Config " +
                    "WHERE config_name = 'NON_STD_CASE_NOTIFICATION')",
            nativeQuery = true
    )
    int updateTopNonStdConfigToApplied(@Param("applied") int applied);
}

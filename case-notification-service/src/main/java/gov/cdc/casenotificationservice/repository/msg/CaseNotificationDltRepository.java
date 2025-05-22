package gov.cdc.casenotificationservice.repository.msg;


import gov.cdc.casenotificationservice.repository.msg.model.CaseNotificationDlt;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
public interface CaseNotificationDltRepository extends JpaRepository<CaseNotificationDlt, UUID> {
    Page<CaseNotificationDlt> findByCreatedOnBetween(Timestamp start, Timestamp end, Pageable pageable);
    @Query(value = "select * from case_notification_dlt where cn_tranportq_out_uid = :uid ", nativeQuery = true)
    List<CaseNotificationDlt> findDltByCnTranportqOutUid(@Param("uid") Long uid);
//
//
//    @Transactional
//    @Modifying
//    @Query(value = """
//    UPDATE dbo.case_notification_dlt
//    SET dlt_status = 'REPROCESSED'
//    WHERE id  = :id
//    """, nativeQuery = true)
//    int updateStatusToQueued(@Param("id") Long id);
}

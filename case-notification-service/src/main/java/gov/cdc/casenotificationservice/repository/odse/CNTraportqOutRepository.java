package gov.cdc.casenotificationservice.repository.odse;

import gov.cdc.casenotificationservice.repository.odse.model.CNTransportqOut;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CNTraportqOutRepository extends JpaRepository<CNTransportqOut, Long> {
    @Query(value = "SELECT TOP 1 * FROM dbo.CN_transportq_out WHERE record_status_cd = :recordStatusCd", nativeQuery = true)
    CNTransportqOut findTopByRecordStatusCd(@Param("recordStatusCd") String recordStatusCd);

    @Query(value = "SELECT TOP 1 * FROM dbo.CN_transportq_out WHERE record_status_cd = :recordStatusCd AND cn_transportq_out_uid = :cnUid", nativeQuery = true)
    CNTransportqOut findTopByRecordStatusCdAndUid(@Param("recordStatusCd") String recordStatusCd, @Param("cnUid") Long cnUid);


    /***
     *
     * Update CN_transportq_out_TEST set record_status_cd = 'PHINMS_QUEUED', record_status_time = getdate()
     * where cn_transportq_out_uid in ($pMessageUID)
     */

    @Transactional
    @Modifying
    @Query(value = """
    UPDATE dbo.CN_transportq_out 
    SET record_status_cd = 'PHINMS_QUEUED', 
        record_status_time = GETDATE() 
    WHERE cn_transportq_out_uid  = :messageUid
    """, nativeQuery = true)
    int updateStatusToQueued(@Param("messageUid") Long messageUid);

}

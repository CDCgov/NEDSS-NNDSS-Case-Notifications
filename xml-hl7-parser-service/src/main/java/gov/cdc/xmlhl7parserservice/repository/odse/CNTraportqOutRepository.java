package gov.cdc.xmlhl7parserservice.repository.odse;

import gov.cdc.xmlhl7parserservice.repository.odse.model.CNTransportqOut;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CNTraportqOutRepository extends JpaRepository<CNTransportqOut, Long> {
    @Query(value = "SELECT TOP 1 * FROM dbo.CN_transportq_out WHERE cn_transportq_out_uid = :cnUid", nativeQuery = true)
    CNTransportqOut findTopByRecordUid(@Param("cnUid") Long cnUid);


}

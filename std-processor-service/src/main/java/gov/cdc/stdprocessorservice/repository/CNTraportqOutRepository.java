package gov.cdc.stdprocessorservice.repository;

import gov.cdc.stdprocessorservice.repository.model.CNTransportqOut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CNTraportqOutRepository extends JpaRepository<CNTransportqOut, Long> {
    @Query(value = "SELECT TOP 1 * FROM dbo.CN_transportq_out WHERE record_status_cd = :recordStatusCd", nativeQuery = true)
    CNTransportqOut findTopByRecordStatusCd(@Param("recordStatusCd") String recordStatusCd);
}

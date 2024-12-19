package gov.cdc.stdprocessorservice.repository;

import gov.cdc.stdprocessorservice.repository.model.CNTransportqOut;
import gov.cdc.stdprocessorservice.repository.model.LookupNNDLookup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LookupNNDLookupRepository extends JpaRepository<LookupNNDLookup, String> {
    @Query(
            value = "SELECT TOP 1 TO_CODE FROM LOOKUP_NNDLookup WHERE FROM_UNIQUE_ID = :fromUniqueId AND CONCEPT_CD = :conceptCd",
            nativeQuery = true
    )
    String findToCodeByFromUniqueIdAndConceptCd(
            @Param("fromUniqueId") String fromUniqueId,
            @Param("conceptCd") String conceptCd
    );

    @Query(
            value = "SELECT TOP 1 TO_CODE FROM LOOKUP_NNDLookup WHERE FROM_UNIQUE_ID = :fromUniqueId AND TO_UNIQUE_ID = :toUniqueId AND CONCEPT_CD = :conceptCd",
            nativeQuery = true
    )
    String findToCodeByFromUniqueIdToUniqueIdAndConceptCd(
            @Param("fromUniqueId") String fromUniqueId,
            @Param("toUniqueId") String toUniqueId,
            @Param("conceptCd") String conceptCd
    );
}

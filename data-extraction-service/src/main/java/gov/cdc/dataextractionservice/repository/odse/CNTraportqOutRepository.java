package gov.cdc.dataextractionservice.repository.odse;

import gov.cdc.dataextractionservice.repository.odse.model.CNTransportqOut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CNTraportqOutRepository extends JpaRepository<CNTransportqOut, Long> {

}
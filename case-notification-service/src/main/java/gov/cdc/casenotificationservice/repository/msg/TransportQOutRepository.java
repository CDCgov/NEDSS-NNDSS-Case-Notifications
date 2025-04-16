package gov.cdc.casenotificationservice.repository.msg;

import gov.cdc.casenotificationservice.repository.msg.model.TransportQOut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransportQOutRepository extends JpaRepository<TransportQOut, Long> {
}
package gov.cdc.casenotificationservice.repository.msg;

import gov.cdc.casenotificationservice.repository.msg.model.NetssTransportQOut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NetssTransportQOutRepository extends JpaRepository<NetssTransportQOut, Long> {
}

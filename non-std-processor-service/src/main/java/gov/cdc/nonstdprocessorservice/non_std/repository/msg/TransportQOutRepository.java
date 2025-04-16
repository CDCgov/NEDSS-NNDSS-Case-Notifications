package gov.cdc.nonstdprocessorservice.non_std.repository.msg;

import gov.cdc.nonstdprocessorservice.non_std.repository.msg.model.ServiceActionPair;
import gov.cdc.nonstdprocessorservice.non_std.repository.msg.model.TransportQOut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransportQOutRepository extends JpaRepository<TransportQOut, Long> {
}

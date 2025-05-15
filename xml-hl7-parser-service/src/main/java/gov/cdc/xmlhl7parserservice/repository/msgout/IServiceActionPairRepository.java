package gov.cdc.xmlhl7parserservice.repository.msgout;

import gov.cdc.xmlhl7parserservice.repository.msgout.model.ServiceActionPairModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IServiceActionPairRepository extends JpaRepository<ServiceActionPairModel, String> {
    Optional<ServiceActionPairModel> findByMessageProfileId(String messageType);
}

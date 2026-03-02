package gov.cdc.xmlhl7parser.repository.msgout;

import gov.cdc.xmlhl7parser.repository.msgout.model.ServiceActionPairModel;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IServiceActionPairRepository
    extends JpaRepository<ServiceActionPairModel, String> {
  Optional<ServiceActionPairModel> findByMessageProfileId(String messageType);
}

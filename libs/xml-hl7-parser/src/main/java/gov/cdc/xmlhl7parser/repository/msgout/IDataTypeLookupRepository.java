package gov.cdc.xmlhl7parser.repository.msgout;

import gov.cdc.xmlhl7parser.repository.msgout.model.DataTypeModel;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IDataTypeLookupRepository extends JpaRepository<DataTypeModel, Integer> {
  Optional<DataTypeModel> findByMmgVersionAndDataType(String mmgVersion, String dataType);
}

package gov.cdc.xmlhl7parserlib.repository.msgout;

import gov.cdc.xmlhl7parserlib.repository.msgout.model.DataTypeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IDataTypeLookupRepository extends JpaRepository<DataTypeModel, Integer> {
    Optional<DataTypeModel> findByMmgVersionAndDataType(String mmgVersion, String dataType);
}

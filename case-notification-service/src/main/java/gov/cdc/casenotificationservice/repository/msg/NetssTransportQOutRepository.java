package gov.cdc.casenotificationservice.repository.msg;

import gov.cdc.casenotificationservice.repository.msg.model.NetssTransportQOut;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NetssTransportQOutRepository extends JpaRepository<NetssTransportQOut, Long> {
  @Query(
      value =
          "select * from NETSS_TransportQ_out where notification_local_id = :notificationLocalId ",
      nativeQuery = true)
  List<NetssTransportQOut> findByNotificationLocalUid(
      @Param("notificationLocalId") String notificationLocalId);
}

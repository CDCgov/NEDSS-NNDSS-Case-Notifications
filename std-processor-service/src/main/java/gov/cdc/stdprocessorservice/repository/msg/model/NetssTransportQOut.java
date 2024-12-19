package gov.cdc.stdprocessorservice.repository.msg.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "NETSS_TransportQ_out")
public class NetssTransportQOut {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NETSS_TransportQ_out_uid", nullable = false)
    private Long netssTransportQOutUid;

    @Column(name = "record_type_cd", nullable = false, length = 200)
    private String recordTypeCd;

    @Column(name = "mmwr_year", nullable = false)
    private Short mmwrYear;

    @Column(name = "mmwr_week", nullable = false)
    private Short mmwrWeek;

    @Column(name = "netss_case_id", nullable = false, length = 50)
    private String netssCaseId;

    @Column(name = "phc_local_id", nullable = false, length = 50)
    private String phcLocalId;

    @Column(name = "notification_local_id", nullable = false, length = 50)
    private String notificationLocalId;

    @Column(name = "add_time", columnDefinition = "datetime default getdate()")
    private Timestamp addTime;

    @Column(name = "payload", nullable = false, length = 250)
    private String payload;

    @Column(name = "record_status_cd", length = 20)
    private String recordStatusCd;
}
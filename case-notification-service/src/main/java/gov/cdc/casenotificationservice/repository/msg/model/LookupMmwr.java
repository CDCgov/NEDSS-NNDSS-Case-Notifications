package gov.cdc.casenotificationservice.repository.msg.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "LOOKUP_MMWR")
public class LookupMmwr {
    @Id
    @Column(name = "WEEK_ENDING", nullable = false)
    private Date weekEnding;

    @Column(name = "MMWR_WEEK", nullable = false)
    private Integer mmwrWeek;

    @Column(name = "MMWR_YEAR", nullable = false)
    private Integer mmwrYear;
}

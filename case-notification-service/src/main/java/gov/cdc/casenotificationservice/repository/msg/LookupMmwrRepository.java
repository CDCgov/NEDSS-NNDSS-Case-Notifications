package gov.cdc.casenotificationservice.repository.msg;

import gov.cdc.casenotificationservice.repository.msg.model.LookupMmwr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;


@Repository
public interface LookupMmwrRepository extends JpaRepository<LookupMmwr, Date> {
    @Query(
            value = "SELECT TOP 1 WEEK_ENDING FROM LOOKUP_MMWR WHERE MMWR_WEEK = :mmwrWeek AND MMWR_YEAR = :mmwrYear",
            nativeQuery = true
    )
    Date findTopWeekEndingByMmwrWeekAndMmwrYear(
            @Param("mmwrWeek") int mmwrWeek,
            @Param("mmwrYear") int mmwrYear
    );
}

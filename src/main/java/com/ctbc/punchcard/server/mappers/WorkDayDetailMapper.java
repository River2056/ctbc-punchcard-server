package com.ctbc.punchcard.server.mappers;

import java.util.List;
import com.ctbc.punchcard.server.models.WorkDayDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkDayDetailMapper extends JpaRepository<WorkDayDetail, Long> {

    @Query(value = "SELECT * FROM work_day_details WHERE start_time LIKE ?", nativeQuery = true)
    List<WorkDayDetail> findAllByDate(String date);

    @Query(value = "SELECT * FROM work_day_details WHERE start_time LIKE ?1 OR end_time LIKE ?1;",
            nativeQuery = true)
    List<WorkDayDetail> findByExactDate(String date);
}

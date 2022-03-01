package com.ctbc.punchcard.server.mappers;

import com.ctbc.punchcard.server.models.WorkDayDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkDayDetailMapper extends JpaRepository<WorkDayDetail, Long> {

}

package com.ctbc.punchcard.server.services;

import com.ctbc.punchcard.server.mappers.WorkDayDetailMapper;
import com.ctbc.punchcard.server.models.WorkDayDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkDayDetailService {

    @Autowired
    private WorkDayDetailMapper workDayDetailMapper;

    public WorkDayDetail createNewWorkDayDetail(WorkDayDetail detail) {
        return this.workDayDetailMapper.save(detail);
    }
}

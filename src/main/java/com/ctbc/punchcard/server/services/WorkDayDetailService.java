package com.ctbc.punchcard.server.services;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import com.ctbc.punchcard.server.mappers.WorkDayDetailMapper;
import com.ctbc.punchcard.server.models.WorkDayDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WorkDayDetailService {

    @Autowired
    private WorkDayDetailMapper workDayDetailMapper;

    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public WorkDayDetail createNewWorkDayDetail(WorkDayDetail detail) {
        return this.workDayDetailMapper.save(detail);
    }

    public List<WorkDayDetail> getAllWorkDayDetails(String year, String month)
            throws ParseException {
        List<WorkDayDetail> list =
                this.workDayDetailMapper.findAllByDate(String.format("%s-%s%s", year, month, "%"));
        list = list.stream().sorted((o1, o2) -> {
            try {

                Date o1Date = df.parse(o1.getStartTime());
                Date o2Date = df.parse(o2.getStartTime());
                return o1Date.compareTo(o2Date);

            } catch (ParseException pe) {
                log.error(String.format("error occurred while parsing string into date: %s",
                        pe.getMessage()));
            }

            return 0;
        }).collect(Collectors.toList());
        return list;
    }
}

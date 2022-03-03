package com.ctbc.punchcard.server.services;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import com.ctbc.punchcard.server.mappers.WorkDayDetailMapper;
import com.ctbc.punchcard.server.models.PunchTimeMode;
import com.ctbc.punchcard.server.models.WorkDayDetail;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WorkDayDetailService {

    @Autowired
    private WorkDayDetailMapper workDayDetailMapper;

    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public WorkDayDetail createOrUpdate(WorkDayDetail detail) {
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

    public WorkDayDetail getWorkDayDetailsByDate(String year, String month, String date) {
        List<WorkDayDetail> list = this.workDayDetailMapper
                .findByExactDate(String.format("%s-%s-%s%s", year, month, date, "%"));
        return list != null && list.size() > 0 ? list.get(0) : null;
    }

    @Transactional
    public void punchTime(Map<String, Object> map, PunchTimeMode mode) throws Exception {
        Long ownerId = Long.parseLong(map.get("ownerId").toString());
        String timeStr = PunchTimeMode.START_TIME.equals(mode) ? map.get("startTime").toString()
                : map.get("endTime").toString();

        /**
         * { "ownerId": 1, "startTime": "2022-03-03 09:30" }
         */
        Pattern pattern = Pattern.compile(
                "\\d{4}-(0[1-9]|1[0-9]|2[0-9]|3[0-1])-(0[1-9]|1[0-9]|2[0-9]|3[0-1])\\s\\d{2}:\\d{2}");
        Matcher matcher = pattern.matcher(timeStr);
        if (matcher.find()) {
            // 2022-03-03 09:30
            // split using space => ["2022-03-03", "09:30"]
            String time = matcher.group();
            String[] timeArr = time.split(" ");

            // 2022-03-03 => ["2022", "03", "03"]
            String[] dateArr = timeArr[0].split("-");
            WorkDayDetail workDayDetail =
                    this.getWorkDayDetailsByDate(dateArr[0], dateArr[1], dateArr[2]);
            if (workDayDetail != null) {
                // has detail, update
                switch (mode) {
                    case START_TIME:
                        workDayDetail.setStartTime(timeStr);
                        break;
                    case END_TIME:
                        workDayDetail.setEndTime(timeStr);
                }
                WorkDayDetail updatedDetail = this.createOrUpdate(workDayDetail);
                log.info(String.format("updated detail: %s",
                        ToStringBuilder.reflectionToString(updatedDetail)));
            } else {
                // no detail, create new
                WorkDayDetail newWorkDayDetail = new WorkDayDetail();
                newWorkDayDetail.setOwnerId(ownerId);
                switch (mode) {
                    case START_TIME:
                        newWorkDayDetail.setStartTime(timeStr);
                        newWorkDayDetail.setEndTime(StringUtils.EMPTY);
                        break;
                    case END_TIME:
                        newWorkDayDetail.setStartTime(StringUtils.EMPTY);
                        newWorkDayDetail.setEndTime(timeStr);
                        break;
                }
                WorkDayDetail insertedDetail = this.createOrUpdate(newWorkDayDetail);
                log.info(String.format("inserted detail: %s",
                        ToStringBuilder.reflectionToString(insertedDetail)));
            }
        }
    }
}

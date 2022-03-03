package com.ctbc.punchcard.server.controllers;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import com.ctbc.punchcard.server.models.PunchTimeMode;
import com.ctbc.punchcard.server.models.WorkDayDetail;
import com.ctbc.punchcard.server.services.WorkDayDetailService;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class MainApiController {

    @Autowired
    private WorkDayDetailService workDayDetailService;

    @GetMapping(value = "/work/{year}/{month}")
    public List<WorkDayDetail> getAllWorkDayDetails(@PathVariable("year") String year,
            @PathVariable("month") String month) {

        List<WorkDayDetail> list = null;

        try {

            log.info(String.format("query work day details: %s-%s", year, month));
            list = this.workDayDetailService.getAllWorkDayDetails(year, month);
            log.info("query work day details end");

        } catch (Exception e) {
            log.error(String.format("error occurred while querying for work day details: %s",
                    e.getMessage()));
        }

        return list;
    }

    @PostMapping(value = "/work")
    public String postNewWorkDay(@RequestBody WorkDayDetail detail) {
        WorkDayDetail savedDetail = null;
        try {

            log.info("start creating new work day details");
            savedDetail = this.workDayDetailService.createOrUpdate(detail);
            log.info(String.format("done saving detail: %s",
                    ToStringBuilder.reflectionToString(savedDetail)));

        } catch (Exception e) {
            log.error(String.format("error occurred while creating new work day details: %s",
                    e.getMessage()));
            return String.format("error occurred while creating new work day details: %s",
                    e.getMessage());
        }

        return ToStringBuilder.reflectionToString(savedDetail);
    }

    @RequestMapping(value = {"/start", "/end"}, method = RequestMethod.POST)
    public Integer addNewStart(@RequestBody Map<String, Object> map, HttpServletRequest request) {
        try {

            String uri = request.getRequestURI();
            log.info(String.format("uri: %s", uri));
            this.workDayDetailService.punchTime(map, PunchTimeMode.getMode(uri));

        } catch (Exception e) {
            log.error(String.format("error occurred while adding start time: %s", e.getMessage()));
            return 500;
        }

        // no exceptions, return 0
        return 0;
    }
}

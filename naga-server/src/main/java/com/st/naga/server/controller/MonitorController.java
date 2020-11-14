package com.st.naga.server.controller;

import cn.hutool.core.date.DateUtil;
import com.st.naga.entity.cluster.HdfsSummary;
import com.st.naga.entity.cluster.QueueMetrics;
import com.st.naga.entity.cluster.YarnSummary;
import com.st.naga.server.BaseController;
import com.st.naga.server.service.MonitorService;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * @author ShaoTian
 * @date 2020/11/12 14:30
 */
@RestController
@RequestMapping("/naga/v1/monitor")
@CrossOrigin
public class MonitorController extends BaseController {

    @Autowired
    private MonitorService monitorService;

    @GetMapping(value = "/storage")
    public Object getHdfsSummary() {
        return getResult(monitorService.findHdfsSummary(DateUtil.toIntSecond(new Date())));
    }

    @GetMapping(value = "/calc")
    public Object getYarnSummary() {
        return getResult(monitorService.findYarnSummary(DateUtil.toIntSecond(new Date())));
    }

    @GetMapping(value = "/storage/chart")
    public Object getHdfsSummaryList() {
        long current = System.currentTimeMillis();
        long zero = current - TimeZone.getDefault().getRawOffset();
        List<HdfsSummary> hdfsSummaryBetween = monitorService.findHdfsSummaryBetween((int) (zero / 1000), (int) (current / 1000));
        List<String> columns = Arrays.stream(FieldUtils.getAllFields(HdfsSummary.class))
                .map(Field::getName).collect(Collectors.toList());
        Map<String, Object> data = new HashMap<>();
        data.put("rows", hdfsSummaryBetween);
        data.put("columns", columns);
        return getResult(data);
    }

    @GetMapping(value = "/calc/chart")
    public Object getYarnSummaryList() {
        long current = System.currentTimeMillis();
        long zero = current - TimeZone.getDefault().getRawOffset();

        List<YarnSummary> yarnSummaryBetween = monitorService.findYarnSummaryBetween((int) (zero / 1000), (int) (current / 1000));

        List<String> columns = Arrays.stream(FieldUtils.getAllFields(YarnSummary.class)).map(Field::getName).collect(Collectors.toList());

        Map<String, Object> data = new HashMap<>();
        data.put("columns", columns);
        data.put("rows", yarnSummaryBetween);
        return getResult(data);
    }

    @GetMapping(value = "/calc/queue")
    public Object getQueueMetrics() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.SECOND, 0);
        List<QueueMetrics> queueMetrics = monitorService.findQueueMetrics((int) (calendar.getTimeInMillis() / 1000));
        List<String> columns = Arrays.stream(FieldUtils.getAllFields(QueueMetrics.class))
                .map(Field::getName).collect(Collectors.toList());
        Map<String, Object> data = new HashMap<>();
        data.put("rows", queueMetrics);
        data.put("columns", columns);
        return getResult(data);
    }

}

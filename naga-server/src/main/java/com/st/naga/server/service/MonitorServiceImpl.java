package com.st.naga.server.service;

import com.st.naga.entity.cluster.HdfsSummary;
import com.st.naga.entity.cluster.QueueMetrics;
import com.st.naga.entity.cluster.YarnSummary;
import com.st.naga.repository.cluster.HdfsSummaryRepository;
import com.st.naga.repository.cluster.QueueMetricsRepository;
import com.st.naga.repository.cluster.YarnSummaryRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author ShaoTian
 * @date 2020/11/11 17:10
 */
@Service
public class MonitorServiceImpl implements MonitorService {

    @Resource
    HdfsSummaryRepository hdfsSummaryRepository;

    @Resource
    QueueMetricsRepository queueMetricsRepository;

    @Resource
    YarnSummaryRepository yarnSummaryRepository;

    @Override
    public void addHdfsSummary(HdfsSummary hdfsSummary) {
        hdfsSummaryRepository.save(hdfsSummary);
    }

    @Override
    public void addYarnSummary(YarnSummary yarnSummary) {
        yarnSummaryRepository.save(yarnSummary);
    }

    @Override
    public void addQueueMetrics(List<QueueMetrics> queueMetrics) {
        queueMetricsRepository.saveAll(queueMetrics);
    }

    @Override
    public HdfsSummary findHdfsSummary(int selectTime) {
        return hdfsSummaryRepository.findTop1ByIsTrashFalseAndCreateTimeLessThanEqualOrderByCreateTimeDesc(selectTime);
    }

    @Override
    public YarnSummary findYarnSummary(int selectTime) {
        return yarnSummaryRepository.findTop1ByIsTrashFalseAndCreateTimeLessThanEqualOrderByCreateTimeDesc(selectTime);
    }

    @Override
    public List<QueueMetrics> findQueueMetrics(int selectTime) {
        return queueMetricsRepository.findByCreateTime(selectTime);
    }

    @Override
    public List<HdfsSummary> findHdfsSummaryBetween(int startTime, int endTime) {
        return hdfsSummaryRepository.findByIsTrashFalseAndCreateTimeBetweenOrderByCreateTimeAsc(startTime, endTime);
    }

    @Override
    public List<YarnSummary> findYarnSummaryBetween(int startTime, int endTime) {
        return yarnSummaryRepository.findByIsTrashFalseAndCreateTimeBetweenOrderByCreateTimeAsc(startTime, endTime);
    }
}

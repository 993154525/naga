package com.st.naga.server.schedule;

import com.st.naga.entity.cluster.HdfsSummary;
import com.st.naga.entity.cluster.QueueMetrics;
import com.st.naga.server.NagaApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author ShaoTian
 * @date 2020/11/12 10:22
 */
@SpringBootTest(classes = NagaApplication.class)
@RunWith(SpringRunner.class)
public class HadoopJmxScheduleTest {

    @Autowired
    private HadoopJmxSchedule hadoopJmxSchedule;


    @Test
    public void hdfsTest() {
        HdfsSummary hdfsSummary = hadoopJmxSchedule.reportHdfsSummary();
        System.out.println(hdfsSummary.toString());
    }

    @Test
    public void yarnTest() {
        System.out.println(hadoopJmxSchedule.reportYarnSummary().toString());
    }

    @Test
    public void queueTest() {
        List<QueueMetrics> queueMetrics = hadoopJmxSchedule.queryQueueMetrics();
        for (QueueMetrics queueMetric : queueMetrics) {

        }
    }
}
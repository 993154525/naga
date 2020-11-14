package com.st.naga.server.schedule;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.st.naga.entity.cluster.HdfsSummary;
import com.st.naga.entity.cluster.QueueMetrics;
import com.st.naga.entity.cluster.YarnSummary;
import com.st.naga.entity.util.StatefulHttpClient;
import com.st.naga.server.service.MonitorService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author ShaoTian
 * @date 2020/11/11 18:34
 */
@Data
@Component
@Slf4j
public class HadoopJmxSchedule {

    @Value("${custom.hadoop.rm.uri}")
    private String rmUriStr;
    @Value("${custom.hadoop.nn.uri}")
    private String nnUriStr;

    @Autowired
    MonitorService monitorService;

    @Autowired
    private RestTemplate restTemplate;

    public static final String JMXSERVERURLFORMAT = "http://%s/jmx?qry=%s";
    public static final String NAMENODEINFO = "Hadoop:service=NameNode,name=NameNodeInfo";
    public static final String FSNAMESYSTEM = "Hadoop:service=NameNode,name=FSNamesystem";
    public static final String FSNAMESYSTEMSTATE = "Hadoop:service=NameNode,name=FSNamesystemState";
    public static final String QUEUEMETRICS = "Hadoop:service=ResourceManager,name=QueueMetrics,q0=root";
    public static final String CLUSTERMETRICS = "Hadoop:service=ResourceManager,name=ClusterMetrics";

    public static final String QUEUEMETRICSALL = "Hadoop:service=ResourceManager,name=QueueMetrics,*";

    public String getActiveNameNodeUri(List<String> nameNodeUri) {
        AtomicReference<String> url = new AtomicReference<>(nameNodeUri.get(0));
        if (nameNodeUri.size() > 1) {
            nameNodeUri.forEach(uri -> {
                String fsNameSystemUrl = String.format(JMXSERVERURLFORMAT, uri, FSNAMESYSTEM);
                ResponseEntity<HadoopMetrics> entity = restTemplate.getForEntity(fsNameSystemUrl, HadoopMetrics.class);
                if ("active".equals(entity.getBody().getMetricsValue("tag.HAState").toString())) {
                    url.set(uri);
                }
            });
        }

        return url.get();
    }

    public String getActiveRmUri(List<String> nameNodeUri) {
        AtomicReference<String> url = new AtomicReference<>(nameNodeUri.get(0));
        if (nameNodeUri.size() > 1) {
            nameNodeUri.forEach(uri -> {
                String fsNameSystemUrl = String.format(JMXSERVERURLFORMAT, uri, FSNAMESYSTEM);
                ResponseEntity<HadoopMetrics> entity = restTemplate.getForEntity(fsNameSystemUrl, HadoopMetrics.class);
                if ("ResourceManager".equals(entity.getBody().getMetricsValue("tag.ClusterMetrics").toString())) {
                    url.set(uri);
                }
            });
        }

        return url.get();
    }

    //定时执行 获取jmx信息
    @Scheduled(cron = "* 30 * * * ?")
    public void hadoopMetricsCollect() {
        //收集hdfs jmx
        HdfsSummary hdfsSummary = reportHdfsSummary();
        if (hdfsSummary != null) {
            monitorService.addHdfsSummary(hdfsSummary);
        }

        //收集yarn jmx
        YarnSummary yarnSummary = reportYarnSummary();
        if (yarnSummary != null) {
            monitorService.addYarnSummary(yarnSummary);
        }

        //收集队列的jmx
        List<QueueMetrics> queueMetricses = queryQueueMetrics();
        monitorService.addQueueMetrics(queueMetricses);
    }


    public HdfsSummary reportHdfsSummary() {
        List<String> nnList = Arrays.asList(nnUriStr);
        if (nnList.isEmpty()) {
            return null;
        }

        String activeNameNodeUri = getActiveNameNodeUri(nnList);
        String url = String.format(JMXSERVERURLFORMAT, activeNameNodeUri, NAMENODEINFO);
        String fsNameSystemStateUrl = String.format(JMXSERVERURLFORMAT, activeNameNodeUri, FSNAMESYSTEMSTATE);

        ResponseEntity<HadoopMetrics> nameNodeEntity = restTemplate.getForEntity(url, HadoopMetrics.class);
        ResponseEntity<HadoopMetrics> fsNameStateEntity = restTemplate.getForEntity(fsNameSystemStateUrl, HadoopMetrics.class);
        ObjectMapper objectMapper = new ObjectMapper();
        HdfsSummary hdfsSummary = null;
        try {
            String mapJson = objectMapper.writeValueAsString(nameNodeEntity.getBody().beans.get(0));
            hdfsSummary = objectMapper.readValue(mapJson, HdfsSummary.class);
            hdfsSummary.setLiveDataNodeNums((int) fsNameStateEntity.getBody().getMetricsValue("NumLiveDataNodes"));
            hdfsSummary.setDeadDataNodeNums((int) fsNameStateEntity.getBody().getMetricsValue("NumDeadDataNodes"));
            hdfsSummary.setVolumeFailuresTotal((int) fsNameStateEntity.getBody().getMetricsValue("VolumeFailuresTotal"));
            hdfsSummary.setCreateTime((int) DateUtil.currentSeconds());
        } catch (IOException e) {
            log.info(e.getMessage());
            hdfsSummary.setTrash(true);
        }

        return hdfsSummary;
    }

    public YarnSummary reportYarnSummary() {
        YarnSummary yarnSummary = new YarnSummary();
        List<String> rmUris = Arrays.asList(rmUriStr.split(";"));
        if (rmUris.isEmpty()) {
            yarnSummary.setTrash(true);
            return yarnSummary;
        }

        String uri = getActiveRmUri(rmUris);
        String clusterMetricsUrl = String.format(JMXSERVERURLFORMAT, uri, CLUSTERMETRICS);
        ResponseEntity<HadoopMetrics> clusterMetrics = restTemplate.getForEntity(clusterMetricsUrl, HadoopMetrics.class);
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            if ("ResourceManager".equals(clusterMetrics.getBody().getMetricsValue("tag.ClusterMetrics").toString())) {
                String queueMetricsUrl = String.format(JMXSERVERURLFORMAT, uri, QUEUEMETRICS);
                ResponseEntity<HadoopMetrics> queueMetricsEntity = restTemplate.getForEntity(queueMetricsUrl, HadoopMetrics.class);
                yarnSummary = objectMapper.readValue(objectMapper.writeValueAsString(queueMetricsEntity.getBody().getBeans().get(0)), YarnSummary.class);
                yarnSummary.setLiveNodeManagerNums((int) clusterMetrics.getBody().getMetricsValue("NumActiveNMs"));
                yarnSummary.setDeadNodeManagerNums((int) clusterMetrics.getBody().getMetricsValue("NumLostNMs"));
                yarnSummary.setUnhealthyNodeManagerNums((int) clusterMetrics.getBody().getMetricsValue("NumUnhealthyNMs"));
                yarnSummary.setCreateTime((int) DateUtil.currentSeconds());
            }
        } catch (IOException e) {
            log.info(e.getMessage());
            yarnSummary.setTrash(true);
        }

        return yarnSummary;
    }

    public List<QueueMetrics> queryQueueMetrics() {
        List<QueueMetrics> queueMetricses = new ArrayList<>();
        List<String> rmUris = Arrays.asList(rmUriStr.split(";"));
        if (rmUris.isEmpty()) {
            return queueMetricses;
        }

        String queueMetricsUrl = String.format(JMXSERVERURLFORMAT, getActiveRmUri(rmUris), QUEUEMETRICSALL);
        ResponseEntity<HadoopMetrics> clusterMetrics = restTemplate.getForEntity(queueMetricsUrl, HadoopMetrics.class);
        ObjectMapper objectMapper = new ObjectMapper();

        List<Map<String, Object>> beans = clusterMetrics.getBody().getBeans();

        beans.forEach(bean -> {
            try {
                QueueMetrics queueMetrics = objectMapper.readValue(objectMapper.writeValueAsString(bean), QueueMetrics.class);
                queueMetrics.setCreateTime((int) DateUtil.currentSeconds());
                queueMetricses.add(queueMetrics);
            } catch (IOException e) {
                log.info(e.getMessage());
                e.printStackTrace();
            }
        });

        return queueMetricses;

    }

}

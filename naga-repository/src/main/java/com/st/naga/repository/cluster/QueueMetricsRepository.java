package com.st.naga.repository.cluster;

import com.st.naga.entity.cluster.QueueMetrics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author ShaoTian
 * @date 2020/11/11 17:01
 */
public interface QueueMetricsRepository extends JpaRepository<QueueMetrics, Long> {

    List<QueueMetrics> findByCreateTime(Integer selectTime);

}

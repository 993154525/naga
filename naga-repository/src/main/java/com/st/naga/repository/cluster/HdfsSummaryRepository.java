package com.st.naga.repository.cluster;

import com.st.naga.entity.cluster.HdfsSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author ShaoTian
 * @date 2020/11/11 17:00
 */
public interface HdfsSummaryRepository extends JpaRepository<HdfsSummary, Long> {

    /**
     * 查找最近的 Hdfs 信息
     *
     * @param selectTime
     * @return
     */
    HdfsSummary findTop1ByIsTrashFalseAndCreateTimeLessThanEqualOrderByCreateTimeDesc(Integer selectTime);

    /**
     * 查找某段时间的hdfs信息
     * @param startTime
     * @param endTime
     * @return
     */
    List<HdfsSummary> findByIsTrashFalseAndCreateTimeBetweenOrderByCreateTimeAsc(Integer startTime, Integer endTime);

}

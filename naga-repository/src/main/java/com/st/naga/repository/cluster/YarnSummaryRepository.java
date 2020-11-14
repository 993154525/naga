package com.st.naga.repository.cluster;

import com.st.naga.entity.cluster.HdfsSummary;
import com.st.naga.entity.cluster.YarnSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author ShaoTian
 * @date 2020/11/11 17:02
 */
public interface YarnSummaryRepository extends JpaRepository<YarnSummary, Long> {

    /**
     * 查找最近的 yarn 信息
     * @param selectTime
     * @return
     */
    YarnSummary findTop1ByIsTrashFalseAndCreateTimeLessThanEqualOrderByCreateTimeDesc(Integer selectTime);

    /**
     * 查找某段时间的yarn信息
     * @param startTime
     * @param endTime
     * @return
     */
    List<YarnSummary> findByIsTrashFalseAndCreateTimeBetweenOrderByCreateTimeAsc(Integer startTime, Integer endTime);


}

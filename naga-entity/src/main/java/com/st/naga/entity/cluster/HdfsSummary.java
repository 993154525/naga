package com.st.naga.entity.cluster;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.st.naga.entity.BaseEntity;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

/**
 * @author ShaoTian
 * @date 2020/11/11 16:53
 */
@Entity
@Data
@ToString
@Table(name = "hdfs_summary")
@JsonIgnoreProperties(ignoreUnknown = true)
public class HdfsSummary extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("Total")
    private Long total;

    @JsonProperty("Used")
    private Long dfsUsed;

    @JsonProperty("PercentUsed")
    private Float percentUsed;

    @JsonProperty("Free")
    private Long dfsFree;

    @JsonProperty("NonDfsUsedSpace")
    private Long nonDfsUsed;

    @JsonProperty("TotalBlocks")
    private Long totalBlocks;

    @JsonProperty("TotalFiles")
    private Long totalFiles;

    @JsonProperty("NumberOfMissingBlocks")
    private Long missingBlocks;

    @JsonProperty("NumLiveDataNodes")
    private Integer liveDataNodeNums;

    @JsonProperty("NumDeadDataNodes")
    private Integer deadDataNodeNums;

    @JsonProperty("VolumeFailuresTotal")
    private Integer volumeFailuresTotal;

}

package com.st.naga.entity.cluster;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.st.naga.entity.BaseEntity;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

/**
 * @author ShaoTian
 * @date 2020/11/11 16:58
 */

@Entity
@Data
@ToString
@Table(name = "yarn_summary")
@JsonIgnoreProperties(ignoreUnknown = true)
public class YarnSummary extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("NumActiveNMs")
    private Integer liveNodeManagerNums;

    @JsonProperty("NumLostNMs")
    private Integer deadNodeManagerNums;

    @JsonProperty("NumUnhealthyNMs")
    private Integer unhealthyNodeManagerNums;

    @JsonProperty("AppsSubmitted")
    private Integer submittedApps;

    @JsonProperty("AppsRunning")
    private Integer runningApps;

    @JsonProperty("AppsPending")
    private Integer pendingApps;

    @JsonProperty("AppsCompleted")
    private Integer completedApps;

    @JsonProperty("AppsKilled")
    private Integer killedApps;

    @JsonProperty("AppsFailed")
    private Integer failedApps;

    @JsonProperty("AllocatedMB")
    private Long allocatedMem;

    @JsonProperty("AllocatedVCores")
    private Integer allocatedCores;

    @JsonProperty("AllocatedContainers")
    private Integer allocatedContainers;

    @JsonProperty("AvailableMB")
    private Long availableMem;

    @JsonProperty("AvailableVCores")
    private Integer availableCores;

    @JsonProperty("PendingMB")
    private Long pendingMem;

    @JsonProperty("PendingVCores")
    private Integer pendingCores;

    @JsonProperty("PendingContainers")
    private Integer pendingContainers;

    @JsonProperty("ReservedMB")
    private Long reservedMem;

    @JsonProperty("ReservedVCores")
    private Integer reservedCores;

    @JsonProperty("ReservedContainers")
    private Integer reservedContainers;

}

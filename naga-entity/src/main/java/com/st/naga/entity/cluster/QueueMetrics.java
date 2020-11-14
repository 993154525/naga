package com.st.naga.entity.cluster;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.st.naga.entity.BaseEntity;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

/**
 * @author ShaoTian
 * @date 2020/11/11 16:55
 */

@Entity
@Data
@ToString
@Table(name = "queue_metrics")
@JsonIgnoreProperties(ignoreUnknown = true)
public class QueueMetrics extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("tag.Queue")
    private String queueName;

    @JsonProperty("AppsPending")
    private Integer appsPending;

    @JsonProperty("AppsRunning")
    private Integer appsRunning;

    @JsonProperty("AllocatedMB")
    private Integer allocatedMB;

    @JsonProperty("AvailableMB")
    private Integer availableMB;

    @JsonProperty("ReservedMB")
    private Integer reservedMB;

    @JsonProperty("PendingMB")
    private Integer pendingMB;

    @JsonProperty("AllocatedContainers")
    private Integer allocatedContainers;

    @JsonProperty("PendingContainers")
    private Integer pendingContainers;

    @JsonProperty("ActiveUsers")
    private Integer ActiveUsers;

    private Integer metricsTime = (int) (System.currentTimeMillis() / 1000);

}

package com.st.naga.entity.meta;

import com.st.naga.entity.BaseEntity;
import lombok.Data;

import javax.persistence.*;

/**
 * @author ShaoTian
 * @date 2020/11/12 15:27
 */
@Entity
@Data
@Table(name="project_info")
public class ProjectInfo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String ns;
    private String detail;

    //hdfs 目录
    private String basePath;
    //yarn 队列
    private String baseQueue;
    //空间配额
    private Long dsQuota;
    //文件数配额
    private Long nsQuota;

    private String admin;
    private String team;

}

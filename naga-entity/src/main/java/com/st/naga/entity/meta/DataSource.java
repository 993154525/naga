package com.st.naga.entity.meta;

import com.st.naga.entity.BaseEntity;
import lombok.Data;

import javax.persistence.*;

/**
 * @author ShaoTian
 * @date 2020/11/12 15:28
 */
@Entity
@Data
@Table(name="data_source")
public class DataSource extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Enumerated(value = EnumType.STRING)
    private SourceType sourceType;
    @Column(columnDefinition = "longtext")
    private String connectInfo;
    private String projectName;
    private Long projectId;
    private String admin;
    private String team;

    @Override
    public String toString(){
        return String.format("DataSource %s",name);
    }

}

package com.st.naga.entity.meta;

import com.st.naga.entity.BaseEntity;
import lombok.Data;

import javax.persistence.*;

/**
 * @author ShaoTian
 * @date 2020/11/12 15:30
 */
@Entity
@Data
@Table(name = "db_info")
public class DbInfo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String detail;
    private String level;
    private String locationUri;
    private String projectName;
    private Long projectId;
    private String admin;
    private String team;

    @Override
    public String toString(){
        return String.format("DBINFO %s", name);
    }
}

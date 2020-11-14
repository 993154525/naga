package com.st.naga.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.MappedSuperclass;

/**
 * @author ShaoTian
 * @date 2020/11/11 16:53
 */
@Data
@MappedSuperclass
public class BaseEntity {
    @JsonIgnore
    private boolean isTrash = false;
    private Integer createTime;
}

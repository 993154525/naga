package com.st.naga.repository.meta;

import com.st.naga.entity.meta.ProjectInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectInfoRepository extends JpaRepository<ProjectInfo,Long> {
    ProjectInfo findByName(String name);
}

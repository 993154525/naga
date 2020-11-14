package com.st.naga.repository.plugin;

import com.st.naga.entity.plugin.PluginPackage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author st
 */
public interface PluginPackageRepository extends JpaRepository<PluginPackage, Long> {
  PluginPackage findByNameAndVersion(String name, String version);

  List<PluginPackage> findByName(String name);
}

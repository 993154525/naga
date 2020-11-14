package com.st.naga.server.plugin;

import com.st.naga.entity.plugin.PackageOutParam;
import com.st.naga.entity.plugin.PackageParam;
import lombok.Data;

import java.util.List;

@Data
public class PackageMetaInfo {
    private String name;
    private String version;
    private String jobType;
    private String language;
    private List<PackageParam> pkgParams;
    private List<PackageOutParam> outParams;
}

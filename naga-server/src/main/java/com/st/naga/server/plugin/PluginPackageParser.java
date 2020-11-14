package com.st.naga.server.plugin;

import com.st.naga.core.exception.ErrorCodes;
import com.st.naga.core.exception.NagaException;
import com.st.naga.core.util.FileUtil;
import com.st.naga.core.util.JsonUtil;
import com.st.naga.entity.plugin.PackageParam;
import com.st.naga.entity.plugin.PluginPackage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.zip.ZipFile;

/**
 * @author ShaoTian
 * @date 2020/11/14 11:44
 */
public class PluginPackageParser {

    public static PluginPackage parse(File zipFile, String packageDir) throws IOException {
        //将zip包 解压到packageDir
        FileUtil.unzip(new ZipFile(zipFile), new File(packageDir));

        //获取插件的meta.json  并解析
        File file = new File(packageDir + File.separator + "meta.json");
        if (!file.exists()) {
            throw new NagaException("meta.json not exists", ErrorCodes.ERROR_PARAM);
        }
        PackageMetaInfo packageMetaInfo = parseMetaInfo(file);
        //根据meta json文件 生成plguinpackage并返回
        PluginPackage pluginPackage = new PluginPackage();
        pluginPackage.setName(packageMetaInfo.getName());
        pluginPackage.setName(packageMetaInfo.getName());
        pluginPackage.setVersion(packageMetaInfo.getVersion());
        pluginPackage.setJobType(packageMetaInfo.getJobType());
        pluginPackage.setLang(packageMetaInfo.getLanguage());
        pluginPackage.setOutParams(packageMetaInfo.getOutParams());
        List<PackageParam> paramSchemas =
                JobTypeParamSchemas.getJobTypeParamSchemas(pluginPackage.getJobType(),
                        pluginPackage.getLang());

        if (packageMetaInfo.getPkgParams() != null && packageMetaInfo.getPkgParams().size() > 0) {
            paramSchemas.addAll(packageMetaInfo.getPkgParams());
        }
        pluginPackage.setDefaultParams(paramSchemas);
        return pluginPackage;
    }

    public static PackageMetaInfo parseMetaInfo(File metaFile) throws IOException {
        byte[] bytes = Files.readAllBytes(metaFile.toPath());
        String json = new String(bytes, "UTF-8");
        return JsonUtil.fromJson(PackageMetaInfo.class, json);
    }
}

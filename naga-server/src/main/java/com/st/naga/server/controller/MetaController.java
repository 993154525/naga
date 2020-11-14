package com.st.naga.server.controller;

import cn.hutool.core.date.DateUtil;
import com.st.naga.core.exception.ErrorCodes;
import com.st.naga.core.exception.NagaException;
import com.st.naga.entity.meta.DataSource;
import com.st.naga.entity.meta.DbInfo;
import com.st.naga.entity.meta.ProjectInfo;
import com.st.naga.server.BaseController;
import com.st.naga.server.service.MetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ShaoTian
 * @date 2020/11/13 14:26
 */
@RestController
@RequestMapping("/naga/v1/meta")
@CrossOrigin
public class MetaController extends BaseController {

    @Autowired
    MetaService metaService;

    //列出Projectinfo（分页）
    @RequestMapping("/projects")
    public Object listProjectInfos(@RequestParam(name = "pageIndex", required = false, defaultValue = "1") int pageIndex,
                                   @RequestParam(name = "pagesize", required = false, defaultValue = "10") int pagesize) {

        Page<ProjectInfo> projectInfos = metaService.listsProjectInfo(null, pageIndex, pagesize, null, null);
        Map<String, Object> map = new HashMap<>();
        map.put("pagesize", pagesize);
        map.put("pages", projectInfos.getContent());
        map.put("pageIndex", pageIndex);
        map.put("pageTotals", projectInfos.getTotalPages());

        return getResult(map);
    }

    // 创建project
    @PostMapping("/project")
    public Object createProject(@RequestBody ProjectInfo projectInfo) throws IOException, InterruptedException {
        projectInfo.setTrash(false);
        projectInfo.setCreateTime(DateUtil.toIntSecond(new Date()));
        projectInfo.setDsQuota(projectInfo.getDsQuota());
        metaService.createProjectInfo(projectInfo);
        return getResult("true");
    }

    // 列出dbinfo 分页
    @GetMapping("/dbs")
    public Object listDbInfos(@RequestParam(name = "pageIndex", required = false, defaultValue = "1") int pageIndex,
                              @RequestParam(name = "pageSize", required = false, defaultValue = "20") int pageSize) {
        //team  需要获取当前登录用户的team
        Page<DbInfo> dbInfos = metaService.listDbInfos(null, pageIndex, pageSize, null, null);
        Map<String, Object> pages = new HashMap<>();
        pages.put("pages", dbInfos.getContent());
        pages.put("pageIndex", pageIndex);
        pages.put("pageSize", pageSize);
        pages.put("pageCount", dbInfos.getTotalPages());
        return getResult(pages);
    }

    // 创建db
    @PostMapping("/db")
    public Object createDb(@RequestBody DbInfo dbInfo) throws IOException, InterruptedException {
        ProjectInfo projectInfoByName = metaService.
                findProjectInfoByName(dbInfo.getProjectName());
        if (projectInfoByName == null) {
            return new RuntimeException("project not exists");
        }
        dbInfo.setTrash(false);
        dbInfo.setCreateTime(DateUtil.toIntSecond(new Date()));
        dbInfo.setProjectId(projectInfoByName.getId());
        dbInfo.setLocationUri(projectInfoByName.getBasePath() +
                "/warehouse/" + dbInfo.getLevel() + "/" + dbInfo.getName() + ".db");
        metaService.createDbInfo(dbInfo);
        return getResult(true);
    }

    @ResponseBody
    @GetMapping("/db")
    public Object getDb(@RequestParam Long id) {
        DbInfo dbInfoById = metaService.findDbInfoById(id);
        return getResult(dbInfoById);
    }

    @ResponseBody
    @DeleteMapping("/db")
    public Object delDb(@RequestParam Long id) {
        metaService.delDbInfo(id);
        return getResult(true);
    }

    @ResponseBody
    @GetMapping("/datasources")
    public Object listDataSources(@RequestParam(name = "pageIndex", defaultValue = "1") int pageIndex,
                                  @RequestParam(name = "pageSize", defaultValue = "20") int pageSize) {
        Page<DataSource> dataSources = metaService.listDataSources(null, pageIndex - 1, pageSize, null, null);
        Map<String, Object> pages = new HashMap<>();
        pages.put("pages", dataSources.getContent());
        pages.put("pageIndex", pageIndex);
        pages.put("pageSize", pageSize);
        pages.put("pageCount", dataSources.getTotalPages());
        return getResult(pages);
    }

    @ResponseBody
    @PostMapping("/datasource")
    public Object createDataSource(@RequestBody DataSource dataSource) {
        ProjectInfo projectInfoByName = metaService.findProjectInfoByName(dataSource.getProjectName());
        if (projectInfoByName == null) {
            throw new RuntimeException("project not exists");
        }
        dataSource.setTrash(false);
        dataSource.setCreateTime(DateUtil.toIntSecond(new Date()));
        dataSource.setProjectId(projectInfoByName.getId());
        metaService.createDataSource(dataSource);
        return getResult(true);
    }

    @ResponseBody
    @PutMapping("/datasource")
    public Object updateDataSource(@RequestBody DataSource dataSource) {
        metaService.updateDataSource(dataSource);
        return getResult(true);
    }

    @ResponseBody
    @GetMapping("/datasource")
    public Object getDataSource(@RequestParam Long id) {
        DataSource dataSourceById = metaService.findDataSourceById(id);
        return getResult(dataSourceById);
    }

    @ResponseBody
    @DeleteMapping("/datasource")
    public Object delDataSource(@RequestParam Long id) {
        metaService.delDataSource(id);
        return getResult(true);
    }

}

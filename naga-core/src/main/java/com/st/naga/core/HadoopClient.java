package com.st.naga.core;

import com.google.common.base.Strings;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.client.HdfsAdmin;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.PrincipalType;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.IOException;
import java.net.URI;
import java.security.PrivilegedExceptionAction;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ShaoTian
 * @date 2020/11/13 10:10
 */
public class HadoopClient {

    private Configuration conf;
    private String hiveMetaStoreUris;
    private UserGroupInformation ugi;
    private String proxyUser;

    public HadoopClient() {
    }

    public HadoopClient(String proxyUser, String hadoopConfPath, String hiveMetaStoreUris) {
        this.conf = new Configuration();
        this.hiveMetaStoreUris = hiveMetaStoreUris;
        ugi = UserGroupInformation.createRemoteUser(proxyUser);
        this.proxyUser = proxyUser;
        conf.addResource(new Path(String.format("%s\\hdfs-site.xml", hadoopConfPath)));
        conf.addResource(new Path(String.format("%s\\core-site.xml", hadoopConfPath)));
    }

    public <T> T doPrivileged(PrivilegedExceptionAction<T> action, String realUser) throws IOException, InterruptedException {
        if (Strings.isNullOrEmpty(realUser)) {
            return ugi.doAs(action);
        }
        UserGroupInformation proxyUser = UserGroupInformation.createProxyUser(realUser, ugi);
        return proxyUser.doAs(action);
    }

    public FileSystem getFileSystem(String realUser, String hdfsUri) throws IOException, InterruptedException {
        return doPrivileged(
                () -> FileSystem.newInstance(URI.create(hdfsUri), conf), realUser);
    }

    public HdfsAdmin getHdfsAdmin(String hdfsUri) throws IOException, InterruptedException {
        return doPrivileged(
                () -> new HdfsAdmin(URI.create(hdfsUri), conf), null);
    }

    public Object createDataBase(String name, String dbPath, String desc, String realUser) throws IOException, InterruptedException {
        return doPrivileged(() -> {
            HiveConf hiveConf = new HiveConf();
            hiveConf.setIntVar(HiveConf.ConfVars.METASTORETHRIFTCONNECTIONRETRIES, 3);
            hiveConf.setVar(HiveConf.ConfVars.METASTOREURIS, hiveMetaStoreUris);
            HiveMetaStoreClient hiveMetaStoreClient = null;
            try {
                hiveMetaStoreClient = new HiveMetaStoreClient(hiveConf);
                Database db = new Database();
                db.setName(name);
                db.setDescription(desc);
                db.setOwnerName(realUser);
                db.setOwnerType(PrincipalType.USER);
                db.setLocationUri(dbPath);
                hiveMetaStoreClient.createDatabase(db);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            } finally {
                if (hiveMetaStoreClient != null) {
                    hiveMetaStoreClient.close();
                }
            }
            return null;
        }, realUser);
    }

    public List<String> showTables(String dbName) {
        HiveMetaStoreClient hiveMetaStoreClient = null;
        try {
            HiveConf hiveConf = new HiveConf();
            hiveConf.setIntVar(HiveConf.ConfVars.METASTORETHRIFTCONNECTIONRETRIES, 3);
            hiveConf.setVar(HiveConf.ConfVars.METASTOREURIS, hiveMetaStoreUris);
            hiveMetaStoreClient = new HiveMetaStoreClient(hiveConf);
            return hiveMetaStoreClient.getAllTables(dbName);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            if (hiveMetaStoreClient != null) {
                hiveMetaStoreClient.close();
            }
        }
    }

    public List<Map<String, String>> getTableSchemas(String dbName, String tableName) {
        HiveMetaStoreClient hiveMetaStoreClient = null;
        try {
            HiveConf hiveConf = new HiveConf();
            hiveConf.setIntVar(HiveConf.ConfVars.METASTORETHRIFTCONNECTIONRETRIES, 3);
            hiveConf.setVar(HiveConf.ConfVars.METASTOREURIS, hiveMetaStoreUris);
            hiveMetaStoreClient = new HiveMetaStoreClient(hiveConf);

            List<FieldSchema> schema = hiveMetaStoreClient.getSchema(dbName, tableName);
            return schema.stream().map(col -> {
                Map<String, String> colInfo = new HashMap<>();
                colInfo.put("name", col.getName());
                colInfo.put("type", col.getType());
                colInfo.put("comment", col.getComment());
                return colInfo;
            }).collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            if (hiveMetaStoreClient != null) {
                hiveMetaStoreClient.close();
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        HadoopClient hadoopClient = new HadoopClient("hadoop", "E:\\job\\hadoop", "thrift://linux15:9083");

        hadoopClient.getHdfsAdmin("");

        FileSystem fileSystem = hadoopClient.getFileSystem(null, "hdfs://192.168.15.15:9000");
        FileStatus[] listStatus = fileSystem.listStatus(new Path("/st"));
        Arrays.stream(listStatus).forEach(fileStatus -> {
            System.out.println(fileStatus.getPath().getName());
        });

        System.out.println("--------------------------");

        List<String> db01 = hadoopClient.showTables("default");
        System.out.println(db01);

        List<Map<String, String>> tableSchemas = hadoopClient.getTableSchemas("default", "m_media_click_log_hive");
        System.out.println(tableSchemas);

    }

}

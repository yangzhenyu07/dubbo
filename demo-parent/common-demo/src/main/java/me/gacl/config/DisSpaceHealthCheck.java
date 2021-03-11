package me.gacl.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * 优雅启动(启动前健康检测)
 * */
@Component
public class DisSpaceHealthCheck implements InitializingBean , ApplicationContextAware {
    private static Logger log = LoggerFactory.getLogger(DisSpaceHealthCheck.class);
    private final static String SQL = "select 1 from dual";
    private  ApplicationContext context;
    @Value("${config.health.filestore.filepath:/}")
    private String filePath;
    @Value("${config.health.filestore.thresholdBytes:10485750}")
    private  long thresholdBytes;
    private  String result = "0";

    /**
     * 健康检测
     * */
    @Override
    public void afterPropertiesSet() throws Exception {
        //检测磁盘
        FileStore fileStore = Files.getFileStore(Paths.get(filePath));
        //空闲大小
        long diskFreeInBytes = fileStore.getUnallocatedSpace();
        if (diskFreeInBytes < thresholdBytes){
            log.error("=========磁盘健康检测不通过 disk free exception=========");
            System.exit(-1);
        }
        //检测数据库
        checkDb();
    }
    //检测数据库
    public void checkDb(){
        DataSource dataSource = (DataSource) context.getBean("dataSource");
        ResultSet rs = null;
        try{
            Connection connection = dataSource.getConnection();
            PreparedStatement pstm = connection.prepareStatement(SQL);
            rs=pstm.executeQuery(SQL);
            while(rs.next()){
                result = rs.getString(1);
            }
            if (!"1".equals(result)){
                throw new Exception("检测数据库状态不通过");
            }
            connection.close();
        }catch (Exception e){
            log.error("=========检测数据库状态不通过=========",e);
            System.exit(-1);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}

package me.gacl.config;

import com.alibaba.druid.util.StringUtils;
import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyResolver;
import me.gacl.utils.Encrypt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * @Description: 配置信息解密实现类
 * @Author yangzhenyu
 **/
@Component
public class EncryptionPropertyResolver implements EncryptablePropertyResolver {
    @Value("${config.decrypt.key:Yzy@1995217}")
    private String key;

    public String resolvePropertyValue(String value) {
        if (StringUtils.isEmpty(value)) {
            return value;
        }
        //过滤
        if (value.startsWith("YZY@")) {
            try {
                return resolveDESValue(value.substring(4));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return value;
    }

    private String resolveDESValue(String value) throws Exception {
        //解密
        Encrypt et = new Encrypt();
        return et.AllDecrypt(value, "windows", key, "Y");
    }
}